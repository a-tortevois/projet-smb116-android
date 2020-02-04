package tortevois.projet.utils;

public interface HttpRequestInterface {
    String METHOD_GET = "GET";
    String METHOD_POST = "POST";
    String METHOD_PUT = "PUT";
    String METHOD_DELETE = "DELETE";

    int TIMEOUT_READ = 10000;
    int TIMEOUT_CONNECTION = 10000;
}
