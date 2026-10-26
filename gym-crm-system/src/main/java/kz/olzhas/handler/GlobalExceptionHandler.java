// package kz.olzhas.handler;

// import kz.olzhas.exceptions.TraineeNotFoundException;
// import kz.olzhas.exceptions.TrainerNotFoundException;
// import kz.olzhas.exceptions.TrainingNotFoundException;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.ControllerAdvice;
// import org.springframework.web.bind.annotation.ExceptionHandler;

// @ControllerAdvice
// public class GlobalExceptionHandler {
//     @ExceptionHandler(TraineeNotFoundException.class)
//     public ResponseEntity<String> handleTraineeNotFoundException(TraineeNotFoundException ex) {
//             return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//         }
//     }