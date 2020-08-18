package com.fermimn.notificationmaker;

import java.util.Objects;

public class Notifica {
    private int id;
    private String titolo, descrizione,data;
    private boolean permanente, visibile;

    public Notifica(String titolo, String descrizione, String data, boolean permanente, boolean visibile, int id) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.data = data;
        this.permanente = permanente;
        this.visibile = visibile;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isPermanente() {
        return permanente;
    }

    public void setPermanente(boolean permanente) {
        this.permanente = permanente;
    }

    public boolean isVisibile() {
        return visibile;
    }

    public void setVisibile(boolean visibile) {
        this.visibile = visibile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notifica notifica = (Notifica) o;
        return titolo.equals(notifica.titolo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titolo);
    }

    @Override
    public String toString() {
        return "Notifica{" +
                "id=" + id +
                ", titolo='" + titolo + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", data='" + data + '\'' +
                ", permanente=" + permanente +
                ", visibile=" + visibile +
                '}';
    }
}
