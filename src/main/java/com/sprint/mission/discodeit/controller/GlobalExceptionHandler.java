import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("해당 리소스를 찾을 수 없습니다. : " + ex.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("잘못된 요청입니다. : " + ex.getMessage());
  }
}
