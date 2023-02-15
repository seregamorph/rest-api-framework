package com.seregamorph.restapi.exceptions;

import static com.seregamorph.restapi.utils.MoreReflectionUtils.tryClassForName;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.seregamorph.restapi.annotations.Compatibility;
import com.seregamorph.restapi.errors.ErrorResponse;
import com.seregamorph.restapi.errors.FieldError;
import com.seregamorph.restapi.errors.ObjectError;
import com.seregamorph.restapi.partial.RedundantFieldsException;
import com.seregamorph.restapi.partial.RequiredFieldsException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

// Note for future maintenance:
// If we use @RestControllerAdvice (not yet available in the version of spring that we support), we could get rid of
// @ControllerAdvice and @ResponseBody.
@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
@ResponseBody
@Slf4j
@RequiredArgsConstructor
public class RestExceptionHandler {

    public static final String VALIDATION_FAILED = "Validation failed";

    /**
     * @see MethodArgumentTypeMismatchException
     */
    @Compatibility("since spring 4.2")
    @Nullable
    private static final Class<? extends TypeMismatchException> METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION_CLASS =
            tryClassForName("org.springframework.web.method.annotation.MethodArgumentTypeMismatchException",
                    TypeMismatchException.class);

    @ExceptionHandler({
            BadRequestException.class,
            ConflictException.class,
            NotFoundException.class,
            RequiredFieldsException.class,
            RedundantFieldsException.class
    })
    public ResponseEntity<?> handleExceptionWithHttpStatus(RuntimeException ex) {
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        HttpStatus httpStatus = responseStatus == null ? INTERNAL_SERVER_ERROR : responseStatus.value();
        ErrorResponse error = new ErrorResponse(httpStatus, ex.getMessage());
        return new ResponseEntity<>(error, httpStatus);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorResponse error = new ErrorResponse(BAD_REQUEST, VALIDATION_FAILED)
                .setFieldErrors(ex.getConstraintViolations().stream()
                        .map(violation -> new FieldError()
                                .setField(getLast(violation.getPropertyPath()).toString())
                                .setRejectedValue(violation.getInvalidValue())
                                .setObjectName(violation.getLeafBean().getClass().getSimpleName())
                                // To stay consistent with MethodArgumentNotValidException
                                .setCode(violation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName())
                                .setMessage(violation.getMessage()))
                        .collect(Collectors.toList()));
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @Compatibility("MethodArgumentTypeMismatchException since 4.2 - hence use super TypeMismatchException")
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(TypeMismatchException ex) {
        ErrorResponse error = new ErrorResponse(BAD_REQUEST, prepareMessage(ex));
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return buildMethodArgumentNotValidResponse(ex.getBindingResult());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorResponse error = new ErrorResponse(BAD_REQUEST, prepareMessage(ex));
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        ErrorResponse error = new ErrorResponse(BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<?> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        ErrorResponse error = new ErrorResponse(BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(error, BAD_REQUEST);
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<?> handleServletRequestBindingException(ServletRequestBindingException ex) {
        val errorResponse = new ErrorResponse(BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleThrowable(Throwable ex) {
        log.error(ex.getMessage(), ex);
        ErrorResponse error = new ErrorResponse(INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(error, INTERNAL_SERVER_ERROR);
    }

    @Compatibility("MethodArgumentTypeMismatchException since 4.2 - hence use super TypeMismatchException")
    private static String prepareMessage(TypeMismatchException exception) {
        val messageBuilder = new StringBuilder();
        if (METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION_CLASS != null
                && METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION_CLASS.isInstance(exception)) {
            val typeMismatchException = (MethodArgumentTypeMismatchException) exception;
            val methodParameter = typeMismatchException.getParameter();
            for (val annotation : methodParameter.getParameterAnnotations()) {
                if (annotation instanceof RequestHeader) {
                    val requestHeader = (RequestHeader) annotation;
                    String parameterName = requestHeader.name();
                    if (parameterName.isEmpty()) {
                        parameterName = methodParameter.getParameterName();
                    }
                    messageBuilder.append("Header '").append(parameterName).append("': ");
                    break;
                } else if (annotation instanceof RequestParam) {
                    val requestParam = (RequestParam) annotation;
                    String parameterName = requestParam.name();
                    if (parameterName.isEmpty()) {
                        parameterName = methodParameter.getParameterName();
                    }
                    messageBuilder.append("Query parameter '").append(parameterName).append("': ");
                    break;
                }
            }
        }

        val cause = exception.getCause();
        if (cause.getCause() instanceof IllegalArgumentException
                || cause.getCause() instanceof DateTimeParseException) {
            messageBuilder.append(cause.getCause().getMessage());
        } else {
            messageBuilder.append(cause.getMessage());
        }
        return messageBuilder.toString();
    }

    private static String prepareMessage(HttpMessageNotReadableException exception) {
        if (exception.getCause() != null && exception.getCause().getCause() != null) {
            // To stay consistent with TypeMismatchException
            return exception.getCause().getCause().getMessage();
        }
        if (exception.getCause() instanceof InvalidFormatException) {
            return ((InvalidFormatException) exception.getCause()).getOriginalMessage();
        }
        return exception.getMessage();
    }

    private static String getMessageFromError(org.springframework.validation.ObjectError error) {
        return ObjectUtils.defaultIfNull(error.getDefaultMessage(), error.getCode());
    }

    private static ResponseEntity<Object> buildMethodArgumentNotValidResponse(BindingResult bindingResult) {
        List<FieldError> fieldErrors = new ArrayList<>();
        List<ObjectError> objectErrors = new ArrayList<>();

        bindingResult.getFieldErrors().forEach(error -> {
            FieldError fieldError = new FieldError()
                    .setField(error.getField())
                    .setRejectedValue(error.getRejectedValue());
            fieldError
                    .setObjectName(error.getObjectName())
                    .setCode(error.getCode())
                    .setMessage(getMessageFromError(error));
            fieldErrors.add(fieldError);
        });

        bindingResult.getGlobalErrors().forEach(error -> {
            ObjectError objectError = new ObjectError()
                    .setObjectName(error.getObjectName())
                    .setCode(error.getCode())
                    .setMessage(getMessageFromError(error));
            objectErrors.add(objectError);
        });

        ErrorResponse errorResponse = new ErrorResponse(BAD_REQUEST, VALIDATION_FAILED)
                .setFieldErrors(fieldErrors)
                .setObjectErrors(objectErrors);

        return new ResponseEntity<>(errorResponse, BAD_REQUEST);
    }

    private static <T> T getLast(Iterable<T> elements) {
        val itr = elements.iterator();
        T last = itr.next();
        while (itr.hasNext()) {
            last = itr.next();
        }
        return last;
    }
}
