package corba8;

public class Relogio {
    
    private int contador;
    
    Relogio() {
        contador = 0;
    }
    
    /*
    public synchronized void incrementa()
    {
        contador++;
    }
    
    
    
    public synchronized void defineValor(int v)
    {
        contador = v;
    }
    
     
     */
    
    public synchronized int retornaValor()
    {
        return contador;
    }
    
    public synchronized int pulsa()
    {
        return ++contador;
    }
     
     
    
    public synchronized void incrementaLevandoEmContaMensagem(Mensagem msg)
    {
        if (msg.contador > contador)
            contador = msg.contador + 1;
        else
            contador++;
    }
}
