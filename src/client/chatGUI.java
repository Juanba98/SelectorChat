   /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.sun.glass.events.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author juanb
 */
public class chatGUI extends javax.swing.JFrame {

  
	private static final long serialVersionUID = 1L;
	private static Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private static Window window;
    private static String host;
    private static String clientName;
    private static int port;
    
    
    
    public chatGUI() {
    	
    	//Ventana para la introduccion de puerto, IP y nombre
        window =  new Window (this, true);
        window.setVisible(true);
        
        //Guardamos los valores obtenidos en la ventana
        host = window.getHost();
        port = window.getPort();
        clientName = window.getUser();
        
        //Configuramos la ventana del chat para su visualizacion
        initComponents();
        chatTextArea.setEditable(false);
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        
    }

    
   
   
   /*Componentes de la ventana*/
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        chatTextArea = new javax.swing.JTextArea();
        inputTextField1 = new javax.swing.JTextField();
        sendButtom = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chat de: " + window.getUser());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        chatTextArea.setColumns(20);
        chatTextArea.setFont(new java.awt.Font("Arial", 1, 15)); // NOI18N
        chatTextArea.setRows(5);
        jScrollPane1.setViewportView(chatTextArea);

        inputTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputTextField1KeyPressed(evt);
            }
        });

        sendButtom.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        sendButtom.setText("ENVIAR");
        sendButtom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtomActionPerformed(evt);
            }
        });
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 430, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(inputTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButtom, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(inputTextField1)
                    .addComponent(sendButtom, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*Rutina al pulsar el boton de envio*/
    private void sendButtomActionPerformed(java.awt.event.ActionEvent evt) {
        sendText();
        
    }
    
  /*Rutina al presionar la tecla intro*/
    private void inputTextField1KeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            sendText();
        }
    }
    
    /*Para enviar el mensaje*/
     private void sendText() {
    	 
    	 
        String msg = clientName;
        
        //Enviamos el nombre del cliente junto al texto introducido
        msg = msg +": "+inputTextField1.getText();
       
        //Limpiamos el inputField
        inputTextField1.setText("");
        
        try {
        	
           //Enivamos el mensaje
        	out.writeUTF(msg);
            out.flush();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(rootPane, "Error sending the message");
        }
       
    }
     

   
     /*Rutina que se ejecuta al cerra la ventana*/
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
        	//Enviamos donde informamos que el cliente abandona el chat 
        	out.writeUTF( "**** " + clientName + " has left ****");
            out.flush();
            
            //Cerramops conexion
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(chatGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /*Rutina para recibir mensajes*/
    private void receibe_msg(){
        try{
           
        	//Abrimos conexion
            socket =  new Socket(host,port);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            String text = "";
            //Enviamos el mensaje que informa que el cliente se ha unido
            out.writeUTF( "**** " + clientName + " has joined ****");
            out.flush();
           
            //Leemos constantemente
            while(true){
              
                if(in != null){
                	
                     text = in.readUTF();
                     //Para una mejor presentacion
                     if(chatTextArea.getText().equals("")){
                         
                        chatTextArea.setText(text);

                     }else{
                    	 if(!text.equals("")) {
                    		 chatTextArea.setText(chatTextArea.getText() +"\n"+ text);

                    	 }
                        
                     }
                     
                }
               
            }
           
        }catch(IOException e){
             JOptionPane.showMessageDialog(rootPane, "Couldn't connect to the server");
             System.exit(0);
        }   
    } 
    public static void main(String args[]) {
        
        chatGUI chat =  new chatGUI();
        chat.receibe_msg();
       
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextArea chatTextArea;
    private javax.swing.JTextField inputTextField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton sendButtom;
    // End of variables declaration//GEN-END:variables

   
}
