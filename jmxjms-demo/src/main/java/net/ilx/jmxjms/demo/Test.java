package net.ilx.jmxjms.demo;
public class Test implements TestMBean {

 private String msg = "default string";

 public Test() {

 }

 public Test(String msg) {
  this.msg = msg;
 }


 public boolean toBoolean() {
  return false;
 }

 @Override
 public String toString() {
  System.out.println("to string was called: " + msg);
  return msg;
 }

 public int toInt() {
  return -1;
 }

}