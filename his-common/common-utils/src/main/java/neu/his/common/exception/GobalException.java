package neu.his.common.exception;

import neu.his.common.result.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GobalException {
    @ExceptionHandler(Exception.class)
    public R error(Exception e)
    {
        e.printStackTrace();
        return R.fail();
    }

    @ExceptionHandler(DefinedException.class)
    public R error(DefinedException e){
        e.printStackTrace();
        return R.fail();
    }
}
