import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.api.client.util.store.FileDataStoreFactory;

import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.*;
import com.google.api.services.gmail.Gmail;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;


public class Quickstart {


//C:\Users\jesus\.credentials\gmail-java-quickstart
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME =
            "Gmail API Java Quickstart";

    /**
     * Directory to store user credentials for this application.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/gmail-java-quickstart");

    /**
     * Global instance of the {@link FileDataStoreFactory}.
     */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport HTTP_TRANSPORT;

    /**
     * Global instance of the scopes required by this quickstart.
     * <p>
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/gmail-java-quickstart
     */
    private static final List<String> SCOPES =
            Arrays.asList(GmailScopes.GMAIL_MODIFY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
                Quickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }

    /**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        Credential credential = authorize();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Método que recupera un mensaje del usuario actualmente logueado.
     *
     * @param service   instacia de objeto tipo Gmail, necesario para realizar la recuperaicón del mensaje.
     * @param userId    identificador del usuario actualmente logueado ("me").
     * @param messageId identificador del mensaje a ser recuperado al realizar el llamado a la función.
     * @return El mensaje según el messageId.
     * @throws IOException Captura  errores en el recuperado del mensaje.
     */
    public static Message getMessage(Gmail service, String userId, String messageId)
            throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();

        return message;
    }


    /**
     * Método que imprime los n (NumMessages) Snipets de los mensajes contenidos en la lista msj
     * y dice si son Spam o no realizado al llamar el método getMessageSnipet.
     *
     * @param msj         Lista que contiene los mensajes de los cuales queremos mostrar el Snipet así como mencionar si son o no Spam.
     * @param service     Servicio de Gmail.
     * @param NumMessages Número de mensajes a mostrar información.
     * @param areSpam     Vector de booleanos donde se guarda la información de si el mensaje íesimo es o no Spam.
     * @throws IOException Captura de errores en el recuperado del Snipet.
     */
    public static void GetMessagesSnipet(List<Message> msj, Gmail service, int NumMessages, boolean[] areSpam) throws IOException {
        List<String> l = new LinkedList<>();
        String mensaje;

        for (int i = 0; i < NumMessages; i++) {
            getMessageSnipet(service, "me", msj.get(i).getId(), areSpam[i]);

        }

    }

    /**
     * Método que muestra el Snipet de un mensaje
     *
     * @param service   Servicio Gmail.
     * @param userId    Id del usuario actualmente logueado ("me").
     * @param messageId Id del mensaje del cual se quiere recuperar el Snipet.
     * @param isSpam    Vector de booleanos donde se guarda la información de si el mensaje íesimo es o no Spam.
     * @throws IOException Captura de errores en el momento del recuperado del Snipet.
     */
    public static void getMessageSnipet(Gmail service, String userId, String messageId, boolean isSpam)
            throws IOException {
        Message message = service.users().messages().get(userId, messageId).execute();

        System.out.println("Message snippet: " + message.getSnippet() + "\nSpam status: " + isSpam);

    }

    /**
     * Método que retorna una lista de Mensajes según el Label enviado como parametro.
     *
     * @param service servicio de GMail.
     * @param Label   etiqueta de los mensajes que se quieren conseguir.
     * @param UserId  id del usuario.
     * @return Lista de mensajes.
     * @throws IOException Captura de errores en el momento de recuperación de los datos.
     */
    public static List<Message> getIdMessages(Gmail service, String Label, String UserId) throws IOException {
        Gmail.Users.Messages.List request = service.users().messages().list(UserId).setLabelIds(Arrays.asList(Label));
        List<Message> list = new LinkedList<>();
        ListMessagesResponse response = request.execute();
        list.addAll(response.getMessages());
        request.setPageToken(response.getNextPageToken());
        return list;
    }


    /**
     * Método que recupera una lista con los mensajes contenidos en la lista msj.
     * @param msj lista de mensajes.
     * @param service  Servicio  de Gmail.
     * @param NumMessages número de mensajes a recuperar.
     * @return Lista de mensajes en formato String.
     * @throws IOException Captura errores en la recuperación de los Mensajes.
     */

    public static List<String> GetMessages(List<Message> msj, Gmail service,String UserId, int NumMessages) throws IOException {
        List<String> l = new LinkedList<>();
        String mensaje;

        for (int i = 0; i < NumMessages; i++) {
            mensaje = getMessage(service, UserId, msj.get(i).getId()).toPrettyString();
            String[] Datos = mensaje.split("\"data\" : \"");


            //Saca el cuerpo del correo
            for (int j = 0; j < Datos.length; j++) {
                Datos[j] = Datos[j].substring(0, Datos[j].indexOf("\""));
            }

            String CorreoCompleto = "";
            //concatenación del correo
            for (int j = 1; j < Datos.length; j++) {

                CorreoCompleto += Datos[j];
            }
            //Decodificación del correo
            String CorreoDecodificado = StringUtils.newStringUtf8(Base64.decodeBase64(CorreoCompleto));
            l.add(CorreoDecodificado);
        }
        List<String> correosDecodificados = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            correosDecodificados.add(Jsoup.parse(l.get(i)).text());
        }
        return correosDecodificados;
    }







    /**
     * Borra las credenciales de un usuario para que sea deslogueado cada vez que este metodo se llama
     */
    public static void deleteCredentials() {
        File f = new File("C:\\Users\\jesus\\.credentials\\gmail-java-quickstart\\StoredCredential");
        if (f.exists())
            f.delete();
    }

    /**
     * Mapa con las frecuencias de las palabras de los correos. Saca la cantidad de correos pasada por parametro del usuario bajo
     * el label dado. Con esos mensajes llama al metodo generateFrequencies de la clase Frequencies y escribe las frecuencias
     * las palabras en el path dado. Finalmente retorna un mapa con las palabras y las frecuencias.
     *
     * @param service       servicio de GMail utilizado
     * @param filename      nombre del archivo donde se va a guadar el mapa
     * @param label         etiqueta de los correos qe se quieren obtener
     * @param messageNumber numero de mensajes que se quieren obtener
     * @return un mapa con las frecuencias de las palabras
     */
    public static Map<String, Integer> getFrequencies(Gmail service, String filename, String label, String UserId, int messageNumber) {
        List<Message> IdM = null;
        Frequencies f = new Frequencies();
        List<String> messageList = null;
        try {
            IdM = Quickstart.getIdMessages(service, label, UserId);
            messageList = Quickstart.GetMessages(IdM, service,UserId, messageNumber);
        } catch (Exception e) {
            e.getMessage();
        }
        Map<String, Integer> frequencies = f.generateFrequencies(messageList);
        if (!filename.equals(""))
            Writer.writeWordFrequencies(frequencies, filename);

        return frequencies;
    }


    /**
     * Devuelve un mapa con las probabilidades de que una palabra sea spam o no spam dado que se encuentre en el
     * mapa dado, que puede ser un mapa de palabras encontradas en el spam o de palabras encontradas en correos
     * que no son spam. Lo hace llamando al metodo generateProbabilities en la clase Frecuencies.
     *
     * @param frequencies mapa de palabas y sus frecuencias.
     * @param listSize    contidad de mensajes leidos en total.
     * @return mapa con las probabilidades de las palabras.
     */
    public static Map<String, Double> getProbabilities(Map<String, Integer> frequencies, int listSize) {
        Frequencies f = new Frequencies();
        Map<String, Double> probabilities = f.generateProbabilities(frequencies, listSize);
        return probabilities;
    }
}