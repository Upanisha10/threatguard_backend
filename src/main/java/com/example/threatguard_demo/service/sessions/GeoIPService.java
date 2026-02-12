package com.example.threatguard_demo.service.sessions;

import com.example.threatguard_demo.models.DTO.GeoLocation;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.AsnResponse;
import com.maxmind.geoip2.model.CityResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

@Service
public class GeoIPService {

    private DatabaseReader dbReader;
    private DatabaseReader cityDbReader;
    private DatabaseReader asnDbReader;


    @PostConstruct
    public void init() throws IOException {

        cityDbReader = new DatabaseReader.Builder(
                new ClassPathResource("GeoLite2-City.mmdb").getInputStream()
        ).build();

        asnDbReader = new DatabaseReader.Builder(
                new ClassPathResource("GeoLite2-ASN.mmdb").getInputStream()
        ).build();

        System.out.println("GeoIP + ASN DB loaded");
    }


    public GeoLocation lookup(String ip) {

        try {

            InetAddress ipAddress = InetAddress.getByName(ip);

            CityResponse city = cityDbReader.city(ipAddress);
            AsnResponse asn = asnDbReader.asn(ipAddress);

            return new GeoLocation(
                    city.getCountry().getName(),
                    city.getCity().getName(),
                    city.getLocation().getLatitude(),
                    city.getLocation().getLongitude(),
                    asn.getAutonomousSystemNumber(),
                    asn.getAutonomousSystemOrganization()
            );

        } catch (Exception e) {

            return new GeoLocation("Unknown", "Unknown", null, null, null, "Unknown");
        }
    }

}

