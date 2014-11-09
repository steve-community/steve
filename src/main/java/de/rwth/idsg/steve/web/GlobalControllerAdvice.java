package de.rwth.idsg.steve.web;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 15.08.2014
 */
@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @InitBinder
    public void binder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new LocalDateEditor());
        binder.registerCustomEditor(LocalTime.class, new LocalTimeEditor());
    }

//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleError(HttpServletRequest req, Exception exception) {
//        log.error("Request: {} raised following exception.", req.getRequestURL(), exception);
//
//        // TODO not finished
//        ModelAndView mav = new ModelAndView();
//        mav.addObject("em", exception.getMessage());
//        mav.addObject("url", req.getRequestURL());
//        mav.setViewName("error");
//        return mav;
//    }
}