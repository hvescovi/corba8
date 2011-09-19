package corba8;

import java.util.Iterator;
import java.util.LinkedList;

public class BufferDeMensagens {
    
    LinkedList<Mensagem> buffer;
    
    BufferDeMensagens() {
        buffer = new LinkedList<Mensagem>();
    }
    
    synchronized public void  add(Mensagem msg)
    {
        buffer.add(msg);
    }
    
    synchronized Mensagem get(int index)
    {
        return buffer.get(index);
    }
    
    synchronized boolean contains(Mensagem elemento)
    {
        return buffer.contains(elemento);
    }
    
    synchronized void mostra(int id)
    {
        Iterator i = buffer.iterator();
        Mensagem m;
        System.out.print("\nBUF "+id+" >");
        int q = 1;
        while (i.hasNext())
        {
            m = (Mensagem) i.next();
            System.out.print(" ["+ q++ +"]:"+m.mensagem+"("+m.contador+","+m.id+")");
        }
    }
    
    synchronized boolean contemMensagemDeConteudo(String conteudo)
    {
        Iterator i = buffer.iterator();
        Mensagem m;
        int q = 1;
        while (i.hasNext())
        {
            m = (Mensagem) i.next();
           // System.out.print(m.mensagem+" eh igual a "+conteudo+"? ");
            if (m.mensagem.equals(conteudo))
            {
            //    System.out.print(" !SIM! ");
                return true;
            }
        }
        return false;
    }

    synchronized boolean contemMensagemDoTipo(String t) {
        int q = buffer.size();
       // System.out.print(" vou procurar em:"+q+" elementos ");
        Mensagem m;
        for (int i = 0; i < q; i++)
        {
            m = buffer.get(i);
           // System.out.print(m.tipoMensagem+" eh igual a "+t+"?");
            if (m.tipoMensagem.equals(t))
            {
            //    System.out.print(" sim tem "+t);
                return true;
            }
        }
    //    System.out.print(" nao tem "+t);
        return false;
    }
    
    synchronized public boolean temCabecaDeFila()
    {
        return !buffer.isEmpty();
    }
    
    public synchronized Mensagem obtemERetiraCabecaDeFila()
    {
        //se huover elemento na fila, retorna a cabeca de fila, e retira essa
        //cabeca da fila
        if (buffer.isEmpty())
            return null;
        else
        {
            return buffer.pop();
        }
    }
    
    public synchronized Mensagem espiaMensagemCabecaDeFila()
    {
        //apenas dah uma espiada, sem retirar a fila
        if (buffer.isEmpty())
            return null;
        else
            return buffer.getFirst();
    }
    
    public synchronized boolean iniciouEFinalizou()
    {
        return (!buffer.isEmpty()) && 
                buffer.getFirst().tipoMensagem.equals("FIM");
    }
    
}
