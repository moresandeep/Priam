package com.priam.cassandra.thrift;

import org.apache.cassandra.thrift.CassandraDaemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NFThinCassandraDaemon extends CassandraDaemon
{
    private static final Logger logger = LoggerFactory.getLogger(NFThinCassandraDaemon.class);

    public static void main(String[] args)
    {
        String token = null;
        String seeds = null;
        boolean isReplace = false;
        while (true)
        {
            try
            {
                token = AWSUtil.getDataFromUrl("http://127.0.0.1:8080/Priam/REST/cassandra_config/test?type=GET_TOKEN");
                seeds = AWSUtil.getDataFromUrl("http://127.0.0.1:8080/Priam/REST/cassandra_config/test?type=GET_SEEDS");
                isReplace = Boolean.parseBoolean(AWSUtil.getDataFromUrl("http://127.0.0.1:8080/Priam/REST/cassandra_config/test?type=IS_REPLACE_TOKEN"));
            }
            catch (Exception e)
            {
                logger.error("Failed to obtain a token from a pre-defined list, we can not start!", e);
            }

            if (token != null && seeds != null)
                break;
            // sleep for 1 sec and try again.
            try
            {
                Thread.sleep(1 * 1000);
            }
            catch (InterruptedException e1)
            {
                // do nothing.
            }
        }
        System.setProperty("cassandra.initial_token", token);

        if (isReplace)
            System.setProperty("cassandra.replace_token", token);

        try
        {
            // add Firewall Friendly JMX RMI Port at {7501}
            String hostname = AWSUtil.getDataFromUrl("http://169.254.169.254/latest/meta-data/public-hostname");
            // JMXProxy.startJMXService(hostname);
        }
        catch (Exception ex)
        {
            logger.error("Couldnt set the JMX WorkAround ", ex);
        }
        catch (Throwable ex)
        {
            logger.error("Couldnt set the JMX WorkAround ", ex);
            // seems like you are running 0.8 so ignore.
        }
        new CassandraDaemon().activate();
    }

    private static String getServiceUrl(final int port, final String hostname)
    {
        return "service:jmx:rmi://" + hostname + ":" + port + "/jndi/rmi://" + hostname + ":" + port + "/jmxrmi";
    }
}