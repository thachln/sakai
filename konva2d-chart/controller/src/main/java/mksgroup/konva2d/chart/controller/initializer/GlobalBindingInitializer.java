/**
 * 
 */
package mksgroup.konva2d.chart.controller.initializer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

/**
 * @author lengocthach
 *
 */
public class GlobalBindingInitializer implements WebBindingInitializer {
    /**
     * [Explain the description for this method here].
     * 
     * @param binder
     * @param request
     * @see org.springframework.web.bind.support.WebBindingInitializer#initBinder(org.springframework.web.bind.WebDataBinder,
     *      org.springframework.web.context.request.WebRequest)
     */
    @Override
    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        // true passed to CustomDateEditor constructor means convert empty
        // String to null
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }
}
