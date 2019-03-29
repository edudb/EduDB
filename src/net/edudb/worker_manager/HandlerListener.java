package net.edudb.worker_manager;

import io.netty.channel.ChannelHandlerContext;
import net.edudb.response.Response;

import java.util.EventListener;

public interface HandlerListener extends EventListener {

    void onResponseArrival(ChannelHandlerContext ctx, Response response);
}
