package controllers;

import java.util.ArrayList;
import models.Barang;
import support.JDBC;
import java.sql.*;

public class BarangController {
    public ArrayList<Barang> getAllBarang() {
        ArrayList<Barang> barangList = new ArrayList<>();
        JDBC db = new JDBC();
        if (db.isConnected) {
            try {
                ResultSet rs = db.getData("SELECT * FROM barang");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String nama = rs.getString("name");
                    int jumlah = rs.getInt("stock");
                    barangList.add(new Barang(id, nama, jumlah));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.disconnect();
            }
        }
        return barangList;
    }

    public ArrayList<String> getBarangUpdates(int id) {
        ArrayList<String> updates = new ArrayList<>();
        JDBC db = new JDBC();
        if (db.isConnected) {
            try {
                ResultSet rs = db.getData("SELECT * FROM barang_updates WHERE barang_id = " + id);
                while (rs.next()) {
                    updates.add(rs.getString("changes"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                db.disconnect();
            }
        }
        return updates;
    }

    public boolean addBarang(Barang barang) {
        JDBC db = new JDBC();
        if (db.isConnected) {
            String query = "INSERT INTO barang (name, stock) VALUES ('" + barang.getNama() + "', " + barang.getJumlah() + ")";
            db.runQuery(query);
            db.disconnect();
            return true;
        }
        return false;
    }

    public boolean updateBarang(Barang barang) {
        JDBC db = new JDBC();
        try {
            ResultSet rs = db.getData("SELECT * FROM barang WHERE id = " + barang.getId());
            if(rs.next()){
                String currentName = rs.getString("name");
                int currentStock = rs.getInt("stock");

                StringBuilder changes = new StringBuilder();
                if(!currentName.equals(barang.getNama())){
                    changes.append("Nama dari ").append(currentName).append(" telah diubah menjadi ").append(barang.getNama()).append(". ");
                }
                if(currentStock != barang.getJumlah()){
                    changes.append("Stock dari ").append(currentStock).append(" telah diubah menjadi ").append(barang.getJumlah()).append(". ");
                }

                db.runQuery("UPDATE barang SET name = '" + barang.getNama() + "', stock = " + barang.getJumlah() + " WHERE id = " + barang.getId());

                if(changes.length() > 0){
                    db.runQuery("INSERT INTO barang_updates (barang_id, changes) VALUES (" + barang.getId() + ", '" + changes.toString() + "')");
                }
                db.disconnect();
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            db.disconnect();
        }
        return false;
    }

    public boolean deleteBarang(int id) {
        JDBC db = new JDBC();
        if (db.isConnected) {
            String query = "DELETE FROM barang WHERE id = " + id;
            db.runQuery(query);
            db.disconnect();
            return true;
        }
        return false;
    }
}
