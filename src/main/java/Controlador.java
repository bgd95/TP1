import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.sun.xml.internal.bind.v2.model.annotation.Quick;

import javax.swing.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import static java.lang.Integer.parseInt;



/*
 * Created by jesus on 01/12/2016.
 */

public class Controlador {
    /*variables necesarias*/
    private final String PARAMETER_FILE = "DefaultParameters.txt";
    private final String WRITING_PATH = "WordFrequencies.txt";
    private final String WRITING_NON_SPAM_FREQUENCIES = "WordFrequenciesInbox.txt";
    private final String USER_ID = "me";
    private int opcion;
    private double valor;
    private int valori;
    private double spamProbability;
    private double spamThreshold;
    private int tamDeEntrenamiento;
    private Quickstart quickstart;
    private Gmail service;

    private Map<String , Integer> SpamFrecuencies;
    private Map<String, Double> spamProbabilities;
    private Map<String , Integer> InboxFrecuencies;
    private Map<String, Double> InboxProbabilities;

    /* strings de Menús*/
    private final String menuPrincipal = "1. Autenticarse \n2. Salir";
    private final String menu = "1. Configurar \n2. Entrenar \n3. Mostrar Datos \n4. Obtener Correo Nuevo \n5. Desloguearse \n6. Salir";
    private final String menuConfiguracion ="1. Modificacion de Probabilidad de SPAM \n2. Modificar SPAM Threshold \n3. Tamaño de número de Entrenamiento de filtro  \n4. Volver al menú anterior";

    /**
     * Constructor para la clase controlador.
     * Saca de un archivo las variables que indican la probabilidad de spam,
     * el threshold para que un correo sea considerado spam y la cantidad de correos
     * usados para el entrenamiento del programa
     */
    public Controlador(){
        opcion = 0;
        valor = 0;
        valori = 0;
        try{
            //Inicializa variables desde archivo
            BufferedReader reader = new BufferedReader(new FileReader(PARAMETER_FILE));
            spamProbability = Double.parseDouble(reader.readLine());
            spamThreshold = Double.parseDouble(reader.readLine());
            tamDeEntrenamiento = Integer.parseInt(reader.readLine());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Método que contiene  el menú principal con la opción principal de loguearse o salir del programa
     */

    public void Menu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Bienvenido a su Filtro de Spam");
        while (opcion < 2) {
            System.out.println("\t \t" + "MENÚ PRINCIPAL \n" + menuPrincipal);
            opcion = sc.nextInt();
            if (opcion == 1) {
                Quickstart.deleteCredentials();
                menuAutenticarse();
            } else if (opcion == 2) {
                System.out.println("Salida con éxito de la aplicación");
            } else {
                System.out.println("Ingrese una una opción valida");
            }
        }
    }


    /**
     * Método que provee al usuario la interfaz para realizar las configuraciones sobre
     * las variables spamProbability, spamThresHold y tamDeEntrenaminto.
     */
    public void menuConfiguracion() {
        Scanner sc = new Scanner(System.in);
        opcion = 0;
        while (opcion < 4) {
            System.out.flush();
            System.out.println("\t \t" + "MENÚ DE CONFIGURACION \n" + menuConfiguracion);
            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    System.out.println("Cambiará la definición de probabilidad de un que un mensaje sea spam");
                    System.out.println("Selecione un nuevo valor mayor a 0 y menor a 1 ");
                    Scanner sc1 = new Scanner(System.in);
                    try{
                        valor = sc1.nextDouble();
                    }catch(InputMismatchException e){
                        System.out.println(e.getMessage());
                    }
                    if (0 < valor && valor < 1) {
                        spamProbability = valor;
                    } else {
                        System.out.println("Valor no valido, el valor sigue siendo " + spamProbability);
                    }
                    break;


                case 2:
                    Scanner sc2 = new Scanner(System.in);
                    System.out.println("Cambiará la definición del Threshold el cual es " + spamThreshold);
                    System.out.println("Selecione un nuevo valor mayor a 0 y menor a 1 ");
                    valor = sc2.nextDouble();
                    if (0 < valor && valor < 1) {
                        spamThreshold = valor;
                    } else {
                        System.out.println("Valor no valido, el valor sigue siendo " + spamThreshold);
                    }
                    break;


                case 3:

                    System.out.println("Cambiará la definición del número de correos para entrenamiento de Filtro \n el cual es " + tamDeEntrenamiento);
                    System.out.println("Selecione un nuevo valor mayor a 0 ");
                    valori = sc.nextInt();

                    if(valori>0){
                        tamDeEntrenamiento = valori;
                    }else{

                    System.out.println("Dato no valido\n El valor sigue siendo "+valori);
                    }
                    break;

                case 4:
                    break;

                default:
                   System.out.println("Seleccionar opción valida");
                    break;
            }
        }
    }

    /**
     * Al elegir entrenar, la aplicación debe conectarse a la cuenta de correo del usuario, obtener
     el n´umero de correos de SPAM configurado en la aplicaci´on o un m´ınimo de 50, y a partir
     de los correos extraer la informaci´on necesaria para el entrenamiento del filtro (ver Rosen,
     basicamente consiste en calcular las probabilidades condiciones de que un correo tenga una
     palabra determinada dado que es SPAM).
     */
    public void menuEntrenar() {
        System.out.flush();
        System.out.println("ENTRENANDO");
        SpamFrecuencies = Quickstart.getFrequencies(service, WRITING_PATH, "SPAM",USER_ID, tamDeEntrenamiento);
        spamProbabilities = Quickstart.getProbabilities(SpamFrecuencies, tamDeEntrenamiento);
        InboxFrecuencies = Quickstart.getFrequencies(service, WRITING_NON_SPAM_FREQUENCIES, "INBOX",USER_ID, tamDeEntrenamiento);
        InboxProbabilities = Quickstart.getProbabilities(InboxFrecuencies, tamDeEntrenamiento);
    }


    /*
    * Mostrar Datos permite al usuario ver una lista de todas las palabras almacenadas en el filtro, en
      forma tabular, mostrando adem´as su frecuencia, su probabilidad y mostrando al final el n´umero
      total de palabras analizadas.
     */

    public void opcionMostrarDatos() {
        Set<Map.Entry<String, Double>> probSet = spamProbabilities.entrySet();
        Iterator<Map.Entry<String, Double>> it = probSet.iterator();
        while (it.hasNext()){
            Map.Entry<String, Double> current = it.next();
            System.out.println(current.getKey() + "\t \t" + SpamFrecuencies.get(current.getKey()) + "\t \t" + current.getValue());
        }
        System.out.println("Cantidad de palabras procesadas: " + probSet.size());
    }


    /**
    *Obtener Correo Nuevo permite al usuario ir a la cuenta de Gmail, traerse todo el correo no leido
    que tenga y clasificarlo como SPAM o no. La aplicaci´on debe mostrarle al usuario un snippet del
    correo nuevo y un mensaje claro de si el correo es SPAM o NO. Si el usuario no ha entrenado
    el filtro, la aplicaci´on debe mostrarle un mensaje al usuario sobre esto, correr el proceso de
    entrenamiento y luego realizar los pasos anteriores. */
    public void ObtenerCorreoNuevo() {
        List<Message> Msjs = new LinkedList<>();
        List<String> Mails = new LinkedList<>();
        try {
            Msjs = Quickstart.getIdMessages(service, "UNREAD", USER_ID);
            int size = 10;
            if(Msjs.size() < 10){
                size = Msjs.size();
            }
            Mails = Quickstart.GetMessages(Msjs, service,USER_ID, size);
            boolean[] areSpam = new boolean[size];
            for(int i = 0; i < size; i++){
                areSpam[i] = Frequencies.spam(spamProbabilities, InboxProbabilities, Mails.get(i), spamProbability, spamThreshold);
            }
            Quickstart.GetMessagesSnipet(Msjs, service, size, areSpam);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que permite al usuario actualmente logueado, desloguear su cuenta de la aplicación.
     */
    public void opcionDesloguearse() {
        //Quickstart.deleteCredentials();
        System.out.println("Credenciales eliminadas\nSe ha deslogueado");
    }

    /**
     * Método que permite al usario de la aplicación loguearse, con el fin de poder acceder a los mensajes del correo,
     * tanto spam así como del IMBOX y UNREAD para generar los datos necesarios para la aplicacón de la formula de Bayes.
     */
    public void menuAutenticarse() {
        Scanner sc =new Scanner( System.in);
        try{
          service = Quickstart.getGmailService();

        }catch (Exception e) {
            e.printStackTrace();
        }
        System.out.flush();
        System.out.println("Precaución \nPodría  trabajar con datos que no corresponden a esta cuenta\n ");
        System.out.print("¿Desea Entrenar la aplicación? \n 1. Si \n De lo contrario presine cualquier tecla\n");
        int opcion=sc.nextInt();
        if(opcion==1){
            menuEntrenar();
        }else{
            SpamFrecuencies = Reader.getWordFrequencies(WRITING_PATH);
            spamProbabilities = Quickstart.getProbabilities(SpamFrecuencies, tamDeEntrenamiento);
            InboxFrecuencies = Reader.getWordFrequencies(WRITING_NON_SPAM_FREQUENCIES);
            InboxProbabilities = Quickstart.getProbabilities(InboxFrecuencies, tamDeEntrenamiento);
        }
        MenuPrincipal();
    }

    /**
     * Método que provee al usuario de la aplicación la interfaz de las principales acciones
     * que puede llevar a cabo en la aplicación.
     */
    public void MenuPrincipal() {
        Scanner sc = new Scanner(System.in);

        int opcion = 0;
        String respuesta = "";


        while (opcion != 6) {
            System.out.println("\t \t" + "MENÚ PRINCIPAL DE LA APLICACIÓN \n" + menu);
            opcion = sc.nextInt();
            switch (opcion) {

                case 1:
                    menuConfiguracion();
                    Writer.writeParameters(PARAMETER_FILE, spamProbability, spamThreshold, tamDeEntrenamiento);
                    break;

                case 2:
                    menuEntrenar();
                    break;

                case 3:
                    opcionMostrarDatos();
                    break;


                case 4:
                    ObtenerCorreoNuevo();
                    break;

                case 5:
                    opcionDesloguearse();
                    opcion = 6;
                    break;

                case 6:
                    opcion = 6;
                    break;

                default:
                    System.out.println("Seleccionar opción valida");
                    break;
            }
        }
    }

    public static void main(String... args){
        Controlador c = new Controlador();
        c.Menu();
    }

}