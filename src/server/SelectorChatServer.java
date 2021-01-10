package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;


public class SelectorChatServer {
	public static final int port = 1234;

    public static void main(String[] args) throws IOException {
        // Abrir el selector
        Selector selector = Selector.open();
        
        // Abrir el canal pasivo en el selector
        ServerSocketChannel server = ServerSocketChannel.open();
        
        //Le asignamos la direccion
        server.socket().bind(new java.net.InetSocketAddress(port));
        
        // Establecer lectura no bloqueante
        server.configureBlocking(false);

      
        server.register(selector,
                SelectionKey.OP_ACCEPT);

        System.out.println("****Server started****");
        
        //Con esta clase manejaremos las funciones para una mejor modularizacion del proyecto
        TCPSelectorProtocol handler =  new TCPSelectorProtocol(256);

        while (true) {

            //Bloquea hasta que al menos un canal este activo
            selector.select();

            //Conjunto de SelectionKey (Canales activos)
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                //Sacamos la key del set para solo procesarla una vez
                iter.remove();

                try {

                    if (key.isValid()) {
                    	
                        //Ha llegado una peticion de conexion
                        if (key.isAcceptable()) {
                            handler.handleAccept(key, selector);
                            
                          

                           //Ha llegado una peticion de lectura
                        } else if (key.isReadable()) {


                           handler.handleRead(key,selector);
                            
                           
                           

                           //Ha llegado una peticion de escritura
                        }else if(key.isWritable()) {
                        	
                        	ByteBuffer toSend = (ByteBuffer) key.attachment();
                            handler.handleWrite(key, selector,toSend);
                            
                            
                            key.interestOps(SelectionKey.OP_READ);
                        	
                        }
                    }
                }catch (IOException e){
                   

                }

            }
        }
    }

}
