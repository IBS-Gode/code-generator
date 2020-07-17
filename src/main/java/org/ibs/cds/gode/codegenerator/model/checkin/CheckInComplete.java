package org.ibs.cds.gode.codegenerator.model.checkin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ibs.cds.gode.status.BinaryStatus;

/**
 *
 * @author manugraj
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInComplete {
    private BinaryStatus status;
    private String url;
    private String commit;


    public static CheckInComplete failed(){
        return new CheckInComplete(BinaryStatus.FAILURE, null, null);
    }
}
