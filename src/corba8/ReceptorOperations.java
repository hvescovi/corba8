package corba8;
/**
* ReceptorOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Receptor.idl
* Saturday, September 17, 2011 4:12:19 AM BRT
*/

public interface ReceptorOperations 
{
  void enviaMensagem (Mensagem msg);
  boolean prontoParaEntrega ();
  void mostraBuffer ();
  int retornaID ();
  boolean recebeuMensagemComConteudo (String conteudo);
  boolean podeDesalocar ();
} // interface ReceptorOperations
