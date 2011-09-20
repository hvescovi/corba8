package corba8;

import java.util.Arrays;
import java.util.LinkedList;

public class ReceptorServant extends ReceptorPOA {

    public BufferDeMensagens b1, b2, b3;
    LinkedList<String> entregues; //delivery
    LinkedList<Mensagem> entreguesCompleta;
    int idReceptor;
    Relogio rel;
    boolean podeDesalocar;

    ReceptorServant(int n, Relogio r) {
        idReceptor = n;
        b1 = new BufferDeMensagens();
        b2 = new BufferDeMensagens();
        b3 = new BufferDeMensagens();
        rel = r;
        
        //cria o monitor de buffers, o vigilante que faz as
        //entregas, aquele que observa as cabecas de filas
        MonitorDeEntrega vigilante = new MonitorDeEntrega(
                idReceptor, b1, b2, b3, entregues, entreguesCompleta);
        new Thread(vigilante).start();
        
        //fila de delivery
        entregues = new LinkedList<String>();
        entreguesCompleta = new LinkedList<Mensagem>();
        
        podeDesalocar = false;
    }
    
    public synchronized int maior (int n1, int n2)
    {
        if (n1 > n2) return n1; else return n2;
    }
    
    @Override
    public void enviaMensagem(Mensagem msg) {
        
        //atualiza o relogio apenas se a mensagem nao for de mim para mim (
        //do emissor para o proprio receptor; e se nao for mensagem de FIM
        
        rel.incrementaLevandoEmContaMensagem(msg);
        
        if ((idReceptor != msg.id) & (!msg.tipoMensagem.equals("FIM")))
        {
            
            
            
            /*
            //verifica o contador do receptor e o da mensagem, pra ver qual serah o maior
            int avanco = maior(msg.contador, rel.contador)+1;

            //faz a entrega com o novo contador
            msg.contador = avanco;

            //atualiza o relogio do receptor, que eh o mesmo do emissor
            rel.defineValor(avanco);
             
             */
        }
        
        //FIQUEI 3 HORAS aqui porque tava colocando idReceptor ao inves de msg.if
        //no IFFFF
        if (msg.id == 1) b1.add(msg);
        if (msg.id == 2) b2.add(msg);
        if (msg.id == 3) b3.add(msg);
        //System.out.println("\nAdicionei: "+msg+"; buffer.size="+b1.buffer.size());
        System.out.print("\nRECEPTOR: "+msg.mensagem+" recebida por p"+idReceptor+", de p"+msg.id);
        //se recebeu de alguem que nao o proprio emissor, mostra o relogio
        if (idReceptor != msg.id) System.out.print("["+rel.retornaValor()+"]");
    }

    @Override
    public boolean prontoParaEntrega() {
        return iniciouETerminou();
   }
    
    @Override
    public void mostraBuffer() {
        b1.mostra(1);
        b2.mostra(2);
        b3.mostra(3);
    }

    @Override
    public int retornaID() {
        return idReceptor;
    }

    @Override
    public boolean recebeuMensagemComConteudo(String conteudo) {
        return entregues.contains(conteudo);
    }
    
    public boolean iniciouETerminou()
    {
        return b1.iniciouEFinalizou() && b2.iniciouEFinalizou() && b3.iniciouEFinalizou();
    }

    @Override
    public boolean podeDesalocar() {
        return podeDesalocar;
    }
    
    
    //classe AUXILIAR para monitorar a entrega!

    class MonitorDeEntrega implements Runnable {
    
        BufferDeMensagens pb1, pb2, pb3;
        int pidReceptor;
        LinkedList<String> entrega;
        LinkedList<Mensagem> entregaCompleta;

        MonitorDeEntrega(int idRec, 
                BufferDeMensagens xb1, 
                BufferDeMensagens xb2, 
                BufferDeMensagens xb3,
                LinkedList<String> ent,
                LinkedList<Mensagem> entC)
        {
            //faz o vinculo com a classe-pai / invocadora
            pb1 = xb1;
            pb2 = xb2;
            pb3 = xb3;
            pidReceptor = idRec;
            
            entrega = ent;
            entregaCompleta = entC;
        }
        
        @Override
        public void run() {
            //observa os buffers pra ver se pode entregar
            System.out.print("\nAguardando entrega em "+pidReceptor);
            
            int q = 1;
            
            //a principio, eh pra ficar esperando
            boolean termina = false;
            
            //inicializa variaveis de comparacao de contadores
            int c1, c2, c3;
            boolean fila1Ativa, fila2Ativa, fila3Ativa;
            
            Mensagem aEntregar;
            Mensagem mt1, mt2, mt3;
                    
            System.out.print(" (receptor "+pidReceptor+" ativado) ");
            
            fila1Ativa = false;
            fila2Ativa = false;
            fila3Ativa = false;
            boolean comecou = false;
            int vaiEntregar = 0;
            int[] vet = new int[4];
            
            //entra na espera
            do
            {
                //soh entrega se:
                // - houver mensagens nas cabecas de filas
                fila1Ativa = pb1.temCabecaDeFila() && (!pb1.espiaMensagemCabecaDeFila().mensagem.equals("FIM"));
                fila2Ativa = pb2.temCabecaDeFila() && (!pb2.espiaMensagemCabecaDeFila().mensagem.equals("FIM"));
                fila3Ativa = pb3.temCabecaDeFila() && (!pb3.espiaMensagemCabecaDeFila().mensagem.equals("FIM"));
                
                if (!comecou)
                {
                    comecou = fila1Ativa && fila2Ativa && fila3Ativa;
                }
                
                if ((comecou) && (fila1Ativa | fila2Ativa | fila3Ativa) )
                {
                    //retira de acordo com a ordem dos relogios logicos
                    
                    c1 = fila1Ativa?pb1.espiaMensagemCabecaDeFila().contador:9999;
                    c2 = fila2Ativa?pb2.espiaMensagemCabecaDeFila().contador:9999;
                    c3 = fila3Ativa?pb3.espiaMensagemCabecaDeFila().contador:9999;
                    
                    System.out.print("\n ~ ~ ~ Analisando:");
                    if (fila1Ativa) System.out.print(" p1:["+c1+"]:"+pb1.espiaMensagemCabecaDeFila().mensagem);
                    if (fila2Ativa) System.out.print(" p2:["+c2+"]:"+pb2.espiaMensagemCabecaDeFila().mensagem);
                    if (fila3Ativa) System.out.print(" p3:["+c3+"]:"+pb3.espiaMensagemCabecaDeFila().mensagem);
                    
                    aEntregar = null;
           
                    vaiEntregar = 0;
                    
                    //parte FEIA!
                    if (fila1Ativa & fila2Ativa & fila3Ativa)
                    {
                        if ((c3<=c1) & (c3<=c2))  vaiEntregar = 3;
                        if ((c2<=c1) & (c2<=c3))  vaiEntregar = 2;
                        if ((c1<=c2) & (c1<=c3))  vaiEntregar = 1;
                    }
                    else
                    {
                        if (fila1Ativa & fila2Ativa & (!fila3Ativa))
                            vaiEntregar = (c2<=c1)?2:1;
                        else if (fila1Ativa & (!fila2Ativa) & fila3Ativa)
                            vaiEntregar = (c3<=c1)?3:1;
                        else if ((!fila1Ativa) & fila2Ativa & fila3Ativa)
                            vaiEntregar = (c3<=c2)?3:2;
                        
                        else if ((!fila1Ativa) & (!fila2Ativa) & fila3Ativa)
                            vaiEntregar = 3;
                        else if ((!fila1Ativa) & fila2Ativa & (!fila3Ativa))
                            vaiEntregar = 2;                                
                        else if (fila1Ativa & (!fila2Ativa) & (!fila3Ativa))
                            vaiEntregar = 1;
                    }
                    
                            
                    
                    
                    
                            
                    /*
                    if ((c3<=c1) & (c3<=c2))  vaiEntregar = 1;
                    if ((c2<=c1) & (c2<=c3))  vaiEntregar = 2;
                    if ((c1<=c2) & (c1<=c3))  vaiEntregar = 3;
                     */
                    
                    if (vaiEntregar == 1)
                      aEntregar = pb1.obtemERetiraCabecaDeFila();
                    else if (vaiEntregar == 2)
                        aEntregar = pb2.obtemERetiraCabecaDeFila();
                    else if (vaiEntregar == 3)
                        aEntregar = pb3.obtemERetiraCabecaDeFila();
                    
                    
                    //entrega a mensagem selecionada! com MENOR 
                    //valor de relogio logico
                    
                    if (aEntregar != null)
                    {
                      System.out.print("\n                     entregue ==>["+
                            aEntregar.contador+"] "+
                            aEntregar.mensagem);
                      //adiciona na lista de delivery
                      entregues.add(aEntregar.mensagem);
                      entreguesCompleta.add(aEntregar);
                      
                    }
                    else if (aEntregar == null)
                        System.out.print("\nDanou-se, nada pra entregar! c1="+c1+",c2="+c2+",c3="+c3);
                    else System.out.print("\n O QUE DEU");
                    
                }
                  
                //verifica se continua
                termina = pb1.iniciouEFinalizou() &&
                        pb2.iniciouEFinalizou() &&
                        pb3.iniciouEFinalizou();
            } while (!termina);
            
            System.out.print("\n FINALIZADO!");
            System.out.print("\n buffer de entrega: ");
            for (Mensagem m:entreguesCompleta)
            {
                System.out.print(m.mensagem+"["+m.contador+"] ");
            }
            podeDesalocar = true;
        }    
    }
    
}

   