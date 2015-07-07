package com.github.drxaos.desktop2web;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.Router;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.ByteArrayOutputStream;

public class Server extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new Server());
    }

    @Override
    public void start() throws Exception {
        Robot r = new Robot();
        Router router = Router.router(vertx);

        router.route("/").handler(ctx -> {
            try {
                ctx.response().putHeader("Content-Type", "text/html; charset=utf-8").end("" +
                                "<script language=\"JavaScript\">\n" +
                                "    function reload(prev, curr) {\n" +
                                "        var elem, next = new Date().getTime();\n" +
                                "        if (elem = document.getElementById(\"i\" + prev)) {\n" +
                                "            elem.parentNode.removeChild(elem);\n" +
                                "        }\n" +
                                "        if (elem = document.getElementById(\"i\" + curr)) {\n" +
                                "            elem.setAttribute(\"style\", \"\");\n" +
                                "        }\n" +
                                "        document.write('<img style=\"display: none;\" src=\"/scr?t=' + next + '\" id=\"i' + next + '\" onload=\"reload(' + curr + ',' + next + ')\"/>');\n" +
                                "        if (elem = document.getElementById(\"i\" + next)) {\n" +
                                "            elem.onclick = function (event) {\n" +
                                "                var xmlhttp = new XMLHttpRequest();\n" +
                                "                xmlhttp.open(\"GET\", \"/click?x=\" + event.offsetX + \"&y=\" + event.offsetY, true);\n" +
                                "                xmlhttp.send();\n" +
                                "            };\n" +
                                "        }\n" +
                                "    }\n" +
                                "</script>\n" +
                                "<img style=\"display: none;\" src=\"/scr?t=0\" onload=\"reload(-1,0)\"/>\n"
                );
            } catch (Exception e) {
                e.printStackTrace();
                ctx.response().end("Error!");
            }
        });

        router.route("/scr").handler(ctx -> {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(r.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), "png", outputStream);
                ctx.response().end(Buffer.buffer(outputStream.toByteArray()));
            } catch (Exception e) {
                e.printStackTrace();
                ctx.response().end("Error!");
            }
        });

        router.route("/click").handler(ctx -> {
            try {
                r.mouseMove(Integer.parseInt(ctx.request().getParam("x")), Integer.parseInt(ctx.request().getParam("y")));
                r.mousePress(InputEvent.BUTTON1_MASK);
                r.mouseRelease(InputEvent.BUTTON1_MASK);
                ctx.response().end("ok");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.response().end("Error!");
            }
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8888);
    }
}