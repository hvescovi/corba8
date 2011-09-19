package corba8;

class Emissor {
    
    Receptor receptores[];
    Relogio relogio;
    int id;
    
    Emissor(Relogio rel)
    {
        relogio = rel;
        
        receptores = new Receptor[4]; 
        //cria receptor com 1 a mais porque tah 
        //usando de 1 a n, e nao de zero a n-1
    }
    
    void send_local(Mensagem msg)
    {
        //APENAS incremento o contador
        relogio.incrementa();
    }
    
    void send_remote(Mensagem msg)
    {
        //se nao for mensagem de FIM, incrementa
        if (!msg.tipoMensagem.equals("FIM"))
        {
            //bota o novo contador incrementado na mensagem
            msg.contador = relogio.pulsa();
        }
        
      //  usar apenas o incrementaLevandoEmContaMensagem
     //  se possível apenas 1 metodo em classe que for critica/sincronizada/de acesso exclusivo
  //    synchronized
        
        //envio para meu receptor
        System.out.print("\nEMISSOR: "+msg.mensagem+" de p"+id+" para p"+id);
        receptores[id].enviaMensagem(msg); 
        
       // if (!msg.tipoMensagem.equals("LOC"))
        {
        
        //faz o ENVIO das mensagens para os outros receptores!
        
            if (id == 1) {System.out.print("\nEMISSOR: "+msg.mensagem+" de p1 para p2"+" ["+relogio.retornaValor()+"]");
                          receptores[2].enviaMensagem(msg);
                          System.out.print("\nEMISSOR: "+msg.mensagem+" de p1 para p3"+" ["+relogio.retornaValor()+"]");
                          receptores[3].enviaMensagem(msg);}
            if (id == 2) {System.out.print("\nEMISSOR: "+msg.mensagem+" de p2 para p1"+" ["+relogio.retornaValor()+"]");
                          receptores[1].enviaMensagem(msg); 
                          System.out.print("\nEMISSOR: "+msg.mensagem+" de p2 para p3"+" ["+relogio.retornaValor()+"]");
                          receptores[3].enviaMensagem(msg);}
            if (id == 3) {System.out.print("\nEMISSOR: "+msg.mensagem+" de p3 para p1"+" ["+relogio.retornaValor()+"]");
                          receptores[1].enviaMensagem(msg);
                          System.out.print("\nEMISSOR: "+msg.mensagem+" de p3 para p2"+" ["+relogio.retornaValor()+"]");
                          receptores[2].enviaMensagem(msg);}
        }
    }

}
