package com.evnit.ttpm.khcn.thread;

import com.evnit.ttpm.khcn.controllers.service.ToolCallNgoaiController;

public class ThreadSystem extends Thread {

    @Override
    public void run() {
        boolean threadControl = true;

        while (threadControl) {

            try {
                ///Xu ly goi api gui mail ở đây
                ToolCallNgoaiController tool = new ToolCallNgoaiController();
                String token = tool.getToken();
                tool.toolAutoEmail(token);
                tool.toolAutoUpload(token);

            } catch (Exception ex) {
                String t = "";
            } finally {
                try {
                    sleep(3000);
                } catch (Exception e) {
                }

            }

        }
    }


}

