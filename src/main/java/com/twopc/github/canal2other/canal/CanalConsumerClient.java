package com.twopc.github.canal2other.canal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.Assert;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.protocol.Message;

public class CanalConsumerClient {
	
    protected final static Logger logger = LoggerFactory.getLogger(CanalConsumerClient.class);
	protected CanalConnector connector = null;
	protected Thread.UncaughtExceptionHandler handler            = new Thread.UncaughtExceptionHandler() {

        public void uncaughtException(Thread t, Throwable e) {
            logger.error("parse events has an error", e);
        }
    };
	protected	Thread	thread	= null;
	private volatile boolean running = false;
	private String destination = null;
	
	public CanalConsumerClient(String zkServers, String destination){
		this.destination = destination;
		connector  = CanalConnectors.newClusterConnector(zkServers, destination, "", "");
	}
	
	protected void start() {
		Assert.notNull(connector, "connector is null");
        thread = new Thread(new Runnable() {

            public void run() {
                process();
            }
        });

        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        running  = true;
	}
	
	protected void process() {
        int batchSize =  1024;
        while (running) {
            try {
                MDC.put("destination", destination );
                connector.connect();
                /** 必须在这里设置filter才会生效？, see https://github.com/alibaba/canal/issues/311*/
                connector.subscribe(""); 
                while (running) {
                	Long start = System.currentTimeMillis();
                	 Message message = connector.getWithoutAck(batchSize); // 获取指定数量的数据
                	 int size = message.getEntries().size();
                     long batchId = message.getId();
                	/*
                	 * 1. parse event row data
                	 * 2. index to ES
                	 */
                }
            } catch (Exception e) {
            	
                logger.error("process error!", e);
            } finally {
            	
                connector.disconnect();
                MDC.remove("destination");
            }
        }
    }
}
