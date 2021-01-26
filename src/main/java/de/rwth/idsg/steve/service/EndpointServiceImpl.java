package de.rwth.idsg.steve.service;

import de.rwth.idsg.steve.web.dto.EndpointInfo;
import de.rwth.idsg.steve.web.dto.IEndpointInfo;
import org.springframework.stereotype.Service;

@Service
public class EndpointServiceImpl implements EndpointService {
    @Override
    public IEndpointInfo getEndpointInfo() {
        return EndpointInfo.getInstance();
    }
}
