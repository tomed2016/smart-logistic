package cl.tomed2016.smartlogistic.application.planning;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RouteOptimizationPort {

    PlanningProposal optimize(LocalDate planningDate, List<UUID> orderIds, List<UUID> vehicleIds);

    record PlanningProposal(LocalDate planningDate, List<UUID> plannedOrderIds) {
    }
}
