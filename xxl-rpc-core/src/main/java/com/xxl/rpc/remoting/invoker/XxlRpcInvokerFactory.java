package com.xxl.rpc.remoting.invoker;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.net.params.BaseCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * xxl-rpc invoker factory, init service-registry
 *
 * @author xuxueli 2018-10-19
 */
public class XxlRpcInvokerFactory {

    public void start() throws Exception {
        if (serviceRegistry != null) {
            serviceRegistry.start();
        }
    }

    public void  stop() throws Exception {
        if (serviceRegistry != null) {
            serviceRegistry.stop();
        }

        if (stopCallbackList.size() > 0) {
            for (BaseCallback callback: stopCallbackList) {
                callback.run();
            }
        }
    }


    // ---------------------- service registry (static) ----------------------

    private static ServiceRegistry serviceRegistry;

    public static ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public static void setServiceRegistry(ServiceRegistry param) {
        serviceRegistry = param;
    }

    // ---------------------- service registry (static) ----------------------
    private static List<BaseCallback> stopCallbackList = new ArrayList<BaseCallback>();     // JettyClient、ClientPooled

    public static void addStopCallBack(BaseCallback callback){
        stopCallbackList.add(callback);
    }


}
