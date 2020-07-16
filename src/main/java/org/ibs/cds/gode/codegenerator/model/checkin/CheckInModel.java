package org.ibs.cds.gode.codegenerator.model.checkin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ibs.cds.gode.entity.type.Specification;

/**
 *
 * @author manugraj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInModel {
    private Specification app;
    private String username;
    private String email;
    private String message;
    private String purpose;
}
