package cn.cvtt.nuoche.entity.game;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="cat")
public class Cat {

    @Id
    @GeneratedValue
    private  Long    id;
    //昵称
    @Column(name="name")
    private  String  name;
    //编号
    @Column(name="number")
    private  String  number;
    //7基因，
    //嘴巴
    @Column(name="mouth")
    private  String  mouth;
    //尾巴
    @Column(name="tail")
    private  String  tail;
    //眼睛
    @Column(name="eye")
    private  String  eye;
    //耳朵
    @Column(name="ear")
    private  String  ear;
    //肚皮
    @Column(name="belly")
    //胡子
    private  String  belly;
    //花纹
    @Column(name="flower")
    private  String  flower;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMouth() {
        return mouth;
    }

    public void setMouth(String mouth) {
        this.mouth = mouth;
    }

    public String getTail() {
        return tail;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public String getEye() {
        return eye;
    }

    public void setEye(String eye) {
        this.eye = eye;
    }

    public String getEar() {
        return ear;
    }

    public void setEar(String ear) {
        this.ear = ear;
    }

    public String getBelly() {
        return belly;
    }

    public void setBelly(String belly) {
        this.belly = belly;
    }

    public String getFlower() {
        return flower;
    }

    public void setFlower(String flower) {
        this.flower = flower;
    }
}
