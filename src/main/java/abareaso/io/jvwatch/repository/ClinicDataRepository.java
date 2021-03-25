package abareaso.io.jvwatch.repository;

import org.springframework.data.repository.CrudRepository;

import abareaso.io.jvwatch.model.ClinicData;

/**
 * A spring data repository used to store the state information of a clinic published data.
 * @author Greg Meyer
 *
 */
public interface ClinicDataRepository extends CrudRepository<ClinicData, String>
{

}
