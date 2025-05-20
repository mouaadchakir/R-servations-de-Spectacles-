package com.ticketing.config;

import com.ticketing.model.ShowCategory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller advice to add global model attributes to all views
 */
@ControllerAdvice
public class GlobalModelAttributesAdvice {

    /**
     * Add show categories to all views
     */
    @ModelAttribute("allCategories")
    public ShowCategory[] getAllCategories() {
        return ShowCategory.values();
    }
}
