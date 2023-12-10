package com.example.banco;
public interface Operacao {
    void aplicar();
    void reverter();
}

// class Cliente implements Operacao{
// @Override
// public synchronized void aplicar() {
//     // Lógica para aplicar a operação no saldo do cliente
//  }

// @Override
// public synchronized void reverter() {
//     // Lógica para reverter a operação no saldo do cliente
//  }
// }