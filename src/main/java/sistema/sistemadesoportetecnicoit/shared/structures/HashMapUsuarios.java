package sistema.sistemadesoportetecnicoit.shared.structures;

import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import java.util.LinkedList;

public class HashMapUsuarios {

    private static class Entry<K, V> {
        K key;
        V value;
        Entry(K key, V value) { this.key = key; this.value = value; }
    }

    private LinkedList<Entry<String, LinkedList<Ticket>>>[] table;
    private int size;
    private final int CAPACITY = 100;

    @SuppressWarnings("unchecked")
    public HashMapUsuarios() {
        table = new LinkedList[CAPACITY];
        for (int i = 0; i < CAPACITY; i++) {
            table[i] = new LinkedList<>();
        }
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % CAPACITY;
    }

    public void agregarTicket(String dpi, Ticket ticket) {
        int index = hash(dpi);
        for (Entry<String, LinkedList<Ticket>> entry : table[index]) {
            if (entry.key.equals(dpi)) {
                entry.value.add(ticket);
                return;
            }
        }
        LinkedList<Ticket> historial = new LinkedList<>();
        historial.add(ticket);
        table[index].add(new Entry<>(dpi, historial));
        size++;
    }

    public LinkedList<Ticket> get(String dpi) {
        int index = hash(dpi);
        for (Entry<String, LinkedList<Ticket>> entry : table[index]) {
            if (entry.key.equals(dpi)) return entry.value;
        }
        return null;
    }

    public boolean containsKey(String dpi) {
        int index = hash(dpi);
        for (Entry<String, LinkedList<Ticket>> entry : table[index]) {
            if (entry.key.equals(dpi)) return true;
        }
        return false;
    }

    public void display() {
        System.out.println("\n--- Estado Actual de la Tabla Hash (Usuarios) ---");
        for (int i = 0; i < CAPACITY; i++) {
            if (!table[i].isEmpty()) {
                System.out.print("Indice " + i + ": ");
                for (Entry<String, LinkedList<Ticket>> e : table[i])
                    System.out.print("[DPI: " + e.key + " | Tickets: " + e.value.size() + "] -> ");
                System.out.println("null");
            }
        }
        System.out.println("Total de usuarios registrados: " + size);
    }

    public void mostrarHistorial(String dpi) {
        LinkedList<Ticket> historial = get(dpi);
        if (historial == null || historial.isEmpty()) {
            System.out.println("Sin historial para DPI: " + dpi);
            return;
        }
        System.out.println("\n--- Historial | DPI: " + dpi + " ---");
        int c = 1;
        for (Ticket t : historial)
            System.out.println("  Ticket #" + c++ + " | Tipo: " + t.getTipo()
                    + " | Motivo: " + t.getMotivo()
                    + " | Fecha: " + t.getFechaHoraAtencion()
                    + " | Tecnico: " + t.getUsuarioAtendio());
    }
}