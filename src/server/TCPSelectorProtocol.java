package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.sun.javafx.scene.control.SelectedCellsMap;

public class TCPSelectorProtocol {
    private int bufferSize;

    public TCPSelectorProtocol(int bufferSize){
        this.bufferSize = bufferSize;
    }

    public void handleAccept(SelectionKey key, Selector selector) throws IOException {


        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel client = ssc.accept();
        client.configureBlocking(false);

        // Se registra un nuevo canal de escritura
        client.register(key.selector(), SelectionKey.OP_WRITE, ByteBuffer.allocate(this.bufferSize));

        
        //Obtenemos la informacion del usuario y la mostramos por pantalla
        String[] clientInfo = client.getRemoteAddress().toString().split(":");
        System.out.println(clientInfo[0] + " at port " + clientInfo[1] + " has entered the chat");
        
        
       

    }
    
    /*Para menejar la lectura*/

    public void handleRead(SelectionKey key,Selector selector) throws IOException {

    	
    	
        //Esta key es de lectura y puesto que la lectura solo ocurre en SocketChanels podemos asignarlo como tal
        SocketChannel client = (SocketChannel) key.channel();

        //Obtenemos el buffer del cliente
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        
      
        //Preparamos el buffer para leer
        buffer.clear();
     
        int bytesread = 0;
 
        //Obtenemos el numero de bytes del buffer
        bytesread = client.read(buffer);
        buffer.flip();
        
        
        //Mensaje del cliente
        String msg = StandardCharsets.UTF_8.decode(buffer).toString();
        
        //Si quiere abandonar el chat
        boolean isFound = msg.indexOf("has left ****") !=-1? true: false;
        

        //Cerramos la conexion
        if(bytesread == -1 || isFound ){
            System.out.println(client.getRemoteAddress() + " has left the chat");
            key.cancel();
            client.close();
            
            //Le enviamos a los clientes restantes el mensaje de abandono 
            if(isFound) sendToClients(buffer, selector,null);
           

        }
        
       
    
        System.out.println("Message from " + client.getRemoteAddress() + ": " + msg );
       
        //Le damos "permiso" para escribir 
        key.interestOps(SelectionKey.OP_WRITE);
        

    }

    /*Para menejar la escritura*/
    public void handleWrite(SelectionKey key, Selector selector,ByteBuffer toSend ) throws IOException {

    	
    	SocketChannel s = (SocketChannel) key.channel();
    	toSend.flip();
    	//Le enviamos su propio mensaje
    	s.write(toSend);
    	
    	//Se lo enviamos al resto de clientes 
    	sendToClients(toSend, selector, s);
    	
        if(!toSend.hasRemaining()) {
        	
        	//Esperamos que el canal sea de lectura 
        	key.interestOps(SelectionKey.OP_READ);
        }
       
        toSend.compact();
       


    }
    
    /*Para enviar a los clientes*/
    private void sendToClients(ByteBuffer toSend,Selector selector,SocketChannel s) throws IOException{
    	System.out.println("Sending the message to clients ...");
    	
    	
        for (SelectionKey key2:
              selector.keys()) {
     	   	 
             if(key2.isValid() && key2.channel()instanceof SocketChannel && !key2.channel().equals(s) ){
             	SocketChannel clientCha = (SocketChannel) key2.channel();
             	toSend.flip();
                clientCha.write(toSend);
                
                 
                
                 
             }



         }
        }
    	
    }










































