package server.functions;


public class DefaultRestResponse {

  private Boolean error;
  private String message;

  public DefaultRestResponse(Boolean error, String message) {
    this.error = error;
    this.message = message;
  }

  public Boolean getError() {
    return error;
  }

  public void setError(Boolean error) {
    this.error = error;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
