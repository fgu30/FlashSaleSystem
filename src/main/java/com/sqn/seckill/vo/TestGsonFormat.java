package com.sqn.seckill.vo;

import java.io.Serializable;

/**
 * Title: TestGsonFormat
 * Description:
 * {"name":"str","age":0,"job":{"firstJob":"str","secondJob":"str"}}
 *
 * @author sqn
 * @version 1.0.0
 * @date 2021/4/22 0022 下午 9:11
 */
public class TestGsonFormat {
    /**
     * name : zs
     * age : 18
     * job : {"firstJob":"a","secondJob":"b"}
     */

    private String name;
    private int age;
    private JobBean job;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public JobBean getJob() {
        return job;
    }

    public void setJob(JobBean job) {
        this.job = job;
    }

    public static class JobBean implements Serializable {
        /**
         * firstJob : a
         * secondJob : b
         */

        private String firstJob;
        private String secondJob;

        public String getFirstJob() {
            return firstJob;
        }

        public void setFirstJob(String firstJob) {
            this.firstJob = firstJob;
        }

        public String getSecondJob() {
            return secondJob;
        }

        public void setSecondJob(String secondJob) {
            this.secondJob = secondJob;
        }
    }
}
