package razesoldier.eqgbot.esi;

import net.troja.eve.esi.model.StatusResponse;

public interface EsiClient {
    StatusResponse getServerStatus() throws EsiException;
}
