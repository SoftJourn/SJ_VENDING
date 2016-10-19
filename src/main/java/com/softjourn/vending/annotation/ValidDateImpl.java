package com.softjourn.vending.annotation;

import com.softjourn.vending.dto.PurchaseFilterDTO;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class ValidDateImpl implements ConstraintValidator<ValidDate, Object> {

    private DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd");

    @Override
    public void initialize(ValidDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        PurchaseFilterDTO dto = (PurchaseFilterDTO) value;
        if (((PurchaseFilterDTO) value).getType ().equals ("Start-Due")) {
            try {
                Date start = this.dateFormat.parse (dto.getStart());
                Date due = this.dateFormat.parse (dto.getDue());
                return !(start.after (due) | due.before (start));
            } catch (ParseException e) {
                log.error (e.toString ());
                return false;
            }
        } else return true;
    }
}
