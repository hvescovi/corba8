package corba8;
/**
* MensagemHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Receptor.idl
* Saturday, September 17, 2011 4:12:19 AM BRT
*/

public final class MensagemHolder implements org.omg.CORBA.portable.Streamable
{
  public Mensagem value = null;

  public MensagemHolder ()
  {
  }

  public MensagemHolder (Mensagem initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = MensagemHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    MensagemHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return MensagemHelper.type ();
  }

}