package de.rwth.idsg.steve.web;

import de.rwth.idsg.steve.repository.dto.ChargePointSelect;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDateTime;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new LocalDateTimeEditor());
        binder.registerCustomEditor(ChargePointSelect.class, new ChargePointSelectEditor());
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleError(HttpServletRequest req, Exception exception) {
        log.error("Request: {} raised following exception.", req.getRequestURL(), exception);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.setViewName("00-error");
        return mav;
    }
}