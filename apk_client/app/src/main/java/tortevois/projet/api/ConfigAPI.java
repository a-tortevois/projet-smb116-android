package tortevois.projet.api;

public interface ConfigAPI {
    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String URL_BASE_API = "http://alexandre.tortevois.fr/api/";

    //Authentification
    String URL_AUTH = URL_BASE_API +"auth/$CONTRACT_ID/$CONTRACT_KEY";

    //Customer
    String URL_POST_CUSTOMER = URL_BASE_API + "customers";
    String URL_GET_CUSTOMERS = URL_BASE_API + "customers";
    String URL_GET_CUSTOMER_EQUIPEMENTS = URL_BASE_API + "customers/$ID/equipments";
    String URL_GET_CUSTOMER_TICKETS = URL_BASE_API + "customers/$ID/tickets";
    String URL_PUT_CUSTOMER = URL_BASE_API + "customers/$ID";
    String URL_DELETE_CUSTOMER = URL_BASE_API + "customers/$ID";

    //Equipment
    String URL_POST_EQUIPMENT = URL_BASE_API + "equipments";
    String URL_GET_EQUIPMENTS = URL_BASE_API + "equipments";
    String URL_GET_EQUIPMENT = URL_BASE_API + "equipments/$ID";
    String URL_PUT_EQUIPMENT = URL_BASE_API + "equipments/$ID";
    String URL_DELETE_EQUIPMENT = URL_BASE_API + "equipments/$ID";

    //Ticket
    String URL_POST_TICKET = URL_BASE_API + "tickets";
    String URL_GET_TICKETS = URL_BASE_API + "tickets";
    String URL_GET_TICKETS_EQUIPMENTS = URL_BASE_API + "tickets/$ID/equipments";
    String URL_GET_TICKET_MESSAGES = URL_BASE_API + "tickets/$ID/messages";
    String URL_PUT_TICKET = URL_BASE_API + "tickets/$ID";
    String URL_DELETE_TICKET = URL_BASE_API + "tickets/$ID";

    //Message
    String URL_POST_MESSAGE = URL_BASE_API + "messages";


}
