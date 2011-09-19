package corba8;

import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;

public class Principal {
    
    public static void main(String args[])
    {
        /*
         * a mensagem possui: conteudo, contador e id do emissor
         * o emissor possui: send_local e send_remote
         * eventos remotos atualizam o relogio para max(contador local, 
         *   contador que vem na mensagem
         * cada processo tem seu relogio logico, iniciado com 0
         */
        
        //inicializacoes do CORBA
        ORB orb = ORB.init(args, null);
        
        //tenta referenciar o servidor de nomes e o rootPOA
        try
        {
            //as 2 tarefas mais criticas: tentar acessar o nameservice e o rootpoa
            org.omg.CORBA.Object objNS = orb.resolve_initial_references("NameService");
            org.omg.CORBA.Object poaOjb = orb.resolve_initial_references("RootPOA");
        
            //se passou deste ponto, tudo ok com o servico CORBA
            
            //referencia o namecontext
            NamingContext nc = NamingContextHelper.narrow(objNS);

            //referencia o POA
            POA rootPOA = POAHelper.narrow(poaOjb);
            rootPOA.the_POAManager().activate();
            
            //prepara os nomes dos receptores
            NameComponent[] nomeReceptor1 = {new NameComponent("receptor1", "Object")};
            NameComponent[] nomeReceptor2 = {new NameComponent("receptor2", "Object")};
            NameComponent[] nomeReceptor3 = {new NameComponent("receptor3", "Object")};
            
            //gerar um ID de receptor livre; encontrar o proximo receptor disponivel
            //supoe que serah o primeiro emissor
            int novoID = 1;
            
            org.omg.CORBA.Object obj = null;
                    
            try
            {
                //tenta o 1
                obj = nc.resolve(nomeReceptor1);
                
                //se continuou, vamos tentar o 2
                novoID = 2;
                obj = nc.resolve(nomeReceptor2);
                
                //se continou, entao com certeza eh o 3!
                novoID = 3;
            }
            catch (Exception e2) {}
            
             //cria um relogio de Lamport
            Relogio relogio = new Relogio();
            //criar um emissor
            Emissor e = new Emissor(relogio);
            //associa o novo ID ao emissor
            e.id = novoID;
            //criar um receptor, jah associando o novo ID e o relogio que ele vai acessar
            ReceptorServant r = new ReceptorServant(novoID, relogio);
            
            System.out.print("\nnovo id = "+novoID);
            
            //registra o receptor
            NameComponent[] nomeNovoReceptor = {new NameComponent("receptor"+novoID, "Object")};
            obj = rootPOA.servant_to_reference(r);  
            nc.rebind(nomeNovoReceptor, obj);                    
            
            System.out.print("\nreceptor "+novoID+" registrado");
            
            //obter a referencia remota do receptor
            Receptor rep = ReceptorHelper.narrow(nc.resolve(nomeNovoReceptor));
            
            //associar o receptor ao emissor
            e.receptores[e.id] = rep;
            
            //aguarda ate que os 3 receptores estejam no ar e conectados comigo
            System.out.print("\nEmissor "+novoID+" aguardando");
            while ((e.receptores[3] == null) | (e.receptores[1] == null) | (e.receptores[2] == null))
            {
                System.out.print(".");
                Thread.sleep(50);
                
                if (e.receptores[1] == null)
                {
                    try
                    {
                        obj = nc.resolve(nomeReceptor1);
                        e.receptores[1] = ReceptorHelper.narrow(obj);
                        System.out.print("\nReceptor "+e.receptores[1].retornaID()+") do ID "+e.id+" OK");
                    }
                    catch (Exception e9)
                    {
                        //nao faz nada
                       // System.out.print(".aguardando receptor 2.");
                    }
                }
                
                if (e.receptores[2] == null)
                {
                    try
                    {
                        obj = nc.resolve(nomeReceptor2);
                        e.receptores[2] = ReceptorHelper.narrow(obj);
                        System.out.print("\nReceptor 2 do ID "+e.id+" OK");
                    }
                    catch (Exception e4)
                    {
                        //nao faz nada
                       // System.out.print(".aguardando receptor 2.");
                    }
                }
                
                try
                {
                    obj = nc.resolve(nomeReceptor3);
                    e.receptores[3] = ReceptorHelper.narrow(obj);
                    System.out.println("\nReceptor 3 do ID "+e.id+" OK");
                }
                catch (Exception e5)
                {
                    //nao faz nada
                  //  System.out.print(".aguardando receptor 3.");
                }
            }
            
            //jah tenho os 3 processos emissores no AR!
            System.out.print("\nENVIANDO MENSAGENS DE " + e.id);
            
            if (e.id == 1)
            {  //e11 m21 m31 e41 m51 m61
                e.send_local(new Mensagem("LOC", relogio.retornaValor(), e.id, "e11"));
         //       System.out.println("...atraso..."); Thread.sleep(4000);
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m21")); 
        //        System.out.println("...atraso..."); Thread.sleep(4000);
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m31")); 
                e.send_local(new Mensagem("LOC", relogio.retornaValor(), e.id, "e41"));
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m51"));
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m61")); 
                e.send_remote(new Mensagem("FIM", relogio.retornaValor(), e.id, "FIM"));
            }
            if (e.id == 2)
            {
               //e12 m22 e32 m42 m52 e62 m72
                e.send_local(new Mensagem("LOC", relogio.retornaValor(), e.id, "e12"));
          //      System.out.println("...atraso..."); Thread.sleep(4000);
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m22")); 
                e.send_local(new Mensagem("LOC", relogio.retornaValor(), e.id, "e32"));
            //    System.out.println("...atraso..."); Thread.sleep(4000);
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m42")); 
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m52"));
                e.send_local(new Mensagem("LOC", relogio.retornaValor(), e.id, "e62"));
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m72"));
                e.send_remote(new Mensagem("FIM", relogio.retornaValor(), e.id, "FIM"));
            }
            if (e.id == 3)
            {
                // m13 m23 e33 e43 m53 m63
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m13"));
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m23"));
                e.send_local(new Mensagem("LOC", relogio.retornaValor(), e.id, "e33")); //Thread.sleep(1000);
                e.send_local(new Mensagem("LOC", relogio.retornaValor(), e.id, "e43"));
      
                //soh mandar o m53 se o e32 (m42) e o m21 estiverem no buffer
                //RENOMEAR ESSE METODO PARA recebeuMensagemComConteudo()
                int q1 = 0;
                while ((!e.receptores[e.id].recebeuMensagemComConteudo("m42")) | 
                      (!e.receptores[e.id].recebeuMensagemComConteudo("m21")))
                {
                    Thread.sleep(10);
                    if ((q1++) > 100)
                    {
                      System.out.print("...aguardando dependência...");
                      q1 = 0;
                    }
                }
                    
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m53"));// Thread.sleep(1000);
                e.send_remote(new Mensagem("REQ", relogio.retornaValor(), e.id, "m63"));
                e.send_remote(new Mensagem("FIM", relogio.retornaValor(), e.id, "FIM"));
                  
                 
            }
          
            System.out.println("\nPODE ENTREGAR! ");
            
            //mostra o buffer do emissor
           // e.receptores[e.id].mostraBuffer();
            
            while (!e.receptores[e.id].podeDesalocar())
            {
                //nao faz nada, espera quando puder desalocar o receptor
                System.out.print("\n Emissor aguardando para desalocar receptor");
                Thread.sleep(100);
            }
            
            //agora pode desalocar o receptor "remoto"
            System.out.print("\nDesalocando o receptor");
            nc.unbind(nomeNovoReceptor);
            
            System.out.print("\nXAU\n");            
                        
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
