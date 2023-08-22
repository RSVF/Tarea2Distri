package cliente;

import java.io.*;
import java.net.*;
import entidad.Persona;
import entidad.PersonaJSON;

class UDPClient {

    public static void main(String a[]) throws Exception {

        // Datos necesario
        String direccionServidor = "127.0.0.1";

        if (a.length > 0) {
            direccionServidor = a[0];
        }

        int puertoServidor = 9876;

        try {
            BufferedReader inFromUser =
                    new BufferedReader(new InputStreamReader(System.in));

            DatagramSocket clientSocket = new DatagramSocket();

            InetAddress IPAddress = InetAddress.getByName(direccionServidor);
            System.out.println("Intentando conectar a = " + IPAddress + ":" + puertoServidor + " via UDP...");

            // Solicitar al usuario la cantidad de segundos a esperar
            System.out.print("Ingrese la cantidad de segundos a esperar (timeout): ");
            int timeoutSeconds = Integer.parseInt(inFromUser.readLine());
            int timeoutMillis = timeoutSeconds * 1000; // Convertir a milisegundos

            byte[] sendData = new byte[1024];
            byte[] receiveData = new byte[1024];

            System.out.print("Ingrese el número de cédula (debe ser numérico): ");
            String strcedula = inFromUser.readLine();
            Long cedula = 0L;
            try {
                cedula = Long.parseLong(strcedula);
            } catch (Exception e1) {
            }

            System.out.print("Ingrese el nombre: ");
            String nombre = inFromUser.readLine();
            System.out.print("Ingrese el apellido: ");
            String apellido = inFromUser.readLine();

            Persona p = new Persona(cedula, nombre, apellido);

            String datoPaquete = PersonaJSON.objetoString(p);
            sendData = datoPaquete.getBytes();

            System.out.println("Enviar " + datoPaquete + " al servidor. (" + sendData.length + " bytes)");
            DatagramPacket sendPacket =
                    new DatagramPacket(sendData, sendData.length, IPAddress, puertoServidor);

            clientSocket.send(sendPacket);

            DatagramPacket receivePacket =
                    new DatagramPacket(receiveData, receiveData.length);

            System.out.println("Esperamos si viene la respuesta.");

            // Establecer el timeout con el valor ingresado por el usuario
            clientSocket.setSoTimeout(timeoutMillis);

            try {
                // ESPERAMOS LA RESPUESTA, BLOQUENTE
                clientSocket.receive(receivePacket);

                String respuesta = new String(receivePacket.getData());
                Persona presp = PersonaJSON.stringObjeto(respuesta.trim());

                InetAddress returnIPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();

                System.out.println("Respuesta desde =  " + returnIPAddress + ":" + port);
                System.out.println("Asignaturas: ");

                for (String tmp : presp.getAsignaturas()) {
                    System.out.println(" -> " + tmp);
                }

            } catch (SocketTimeoutException ste) {

                System.out.println("TimeOut: El paquete udp se asume perdido.");
            }
            clientSocket.close();
        } catch (UnknownHostException ex) {
            System.err.println(ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }
}
