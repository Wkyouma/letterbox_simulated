import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.*;

class LetterBox {
    public static void main(String[] args) {
        new Frame();
    }
}

class Frame extends JFrame implements ActionListener {

    ArrayList<Filme> filmes;

    JComboBox<String> inputNome;
    JTextField inputNota;
    JTextArea inputDesc;

    JButton inputBotao;
    JButton exportBotao;
    JButton loadCSVBotao;

    JTextArea displayFilmes;

    Frame() {

        filmes = new ArrayList<Filme>();
        Font fonte = new Font("Cascadia Code", Font.PLAIN, 18);

        JLabel inputNomeLabel = new JLabel("Nome do filme: ");
        inputNomeLabel.setBounds(10, 10, 200, 30);
        inputNomeLabel.setFont(fonte);
        this.add(inputNomeLabel);

        inputNome = new JComboBox<String>();
        inputNome.setBounds(220, 10, 200, 30);
        inputNome.setFont(fonte);
        inputNome.setEditable(true);
        this.add(inputNome);

        JLabel inputNotaLabel = new JLabel("Nota do filme: ");
        inputNotaLabel.setBounds(10, 50, 200, 30);
        inputNotaLabel.setFont(fonte);
        this.add(inputNotaLabel);

        inputNota = new JTextField();
        inputNota.setBounds(220, 50, 60, 30);
        inputNota.setFont(fonte);
        this.add(inputNota);

        JLabel inputDescLabel = new JLabel("Descrição do filme: ");
        inputDescLabel.setBounds(10, 205, 250, 30);
        inputDescLabel.setFont(fonte);
        this.add(inputDescLabel);

        inputDesc = new JTextArea();
        inputDesc.setFont(fonte);
        inputDesc.setEditable(true);
        inputDesc.setLineWrap(true);
        inputDesc.setWrapStyleWord(true);
        inputDesc.setAutoscrolls(true);
        
        JScrollPane scrollDesc = new JScrollPane(inputDesc);
        scrollDesc.setBounds(10, 240, 450, 430);
        scrollDesc.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDesc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollDesc);

        inputBotao = new JButton();
        inputBotao.setText("Adicionar filme");
        inputBotao.setFont(fonte);
        inputBotao.setBounds(10, 90, 200, 50);
        inputBotao.addActionListener(this);
        this.add(inputBotao);

        exportBotao = new JButton();
        exportBotao.setText("Exportar para OBJ");
        exportBotao.setFont(fonte);
        exportBotao.setBounds(220, 90, 240, 50);
        exportBotao.addActionListener(this);
        this.add(exportBotao);

        loadCSVBotao = new JButton();
        loadCSVBotao.setText("Carregar CSV");
        loadCSVBotao.setFont(fonte);
        loadCSVBotao.setBounds(10, 150, 200, 50);
        loadCSVBotao.addActionListener(this);
        this.add(loadCSVBotao);

        displayFilmes = new JTextArea();
        displayFilmes.setFont(fonte);
        displayFilmes.setEditable(false);
        displayFilmes.setLineWrap(true);
        displayFilmes.setWrapStyleWord(true);
        displayFilmes.setAutoscrolls(true);
        
        JScrollPane scrollDisplay = new JScrollPane(displayFilmes);
        scrollDisplay.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollDisplay.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollDisplay.setBounds(510, 20, 700, 630);

        this.add(scrollDisplay);
        
        importarFilmes(filmes);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1280, 720);
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == inputBotao) {
            try {
                adicionarFilme();
            } catch (ExcecaoNotaInvalida e) {
                JOptionPane.showMessageDialog(this, "Nota do filme deve ser um número entre 0 e 10.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else if (event.getSource() == exportBotao) {
            exportarFilmes();
        } else if (event.getSource() == loadCSVBotao) {
            loadFilmesFromCSV();
        }
    }

    private void adicionarFilme() throws ExcecaoNotaInvalida {
        int selecao = inputNome.getSelectedIndex();
        if (selecao < 0) {
            Object item = inputNome.getSelectedItem();
            if (item != null) {
                String nomeChecagem = item.toString();
                try {
                    validarTexto(nomeChecagem);
                } catch (ExcecaoCampoVazio e) {
                    JOptionPane.showMessageDialog(this, "Nome do filme não pode estar vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                inputNome.addItem(nomeChecagem);
                Filme filme = new Filme(nomeChecagem);
                filmes.add(filme);
                displayFilmes.setText("");
                for (Filme fi : filmes) {
                    displayFilmes.append(fi.toString() + "\n");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Algum filme deve ser selecionado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String nome = inputNome.getSelectedItem().toString();
        double nota;
        try {
            nota = Double.parseDouble(inputNota.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Nota do filme deve ser um número.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (nota < 0 || nota > 10) {
            throw new ExcecaoNotaInvalida();
        }
                
        String desc = inputDesc.getText();
        try {
            validarTexto(desc);
        } catch (ExcecaoCampoVazio e) {
            JOptionPane.showMessageDialog(this, "Descrição do filme não pode estar vazio", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Filme filme = new Filme(nome, nota, desc);
        for (Filme iFilme : filmes) {
            if (iFilme.getNome() == filme.getNome()) {
                filmes.remove(iFilme);
                filmes.add(filme);
                break;
            }
        }
        displayFilmes.setText("");
        for (Filme fi : filmes) {
            displayFilmes.append(fi.toString() + "\n\n");
        }
        System.out.println(filmes.get(filmes.size() - 1));
    }

    private void validarTexto(String texto) throws ExcecaoCampoVazio {
        if (texto.length() <= 0) {
            throw new ExcecaoCampoVazio();
        }
    }

    private void exportarFilmes() {
        try {
            FileOutputStream arquivo = new FileOutputStream("filmes.obj");
            ObjectOutputStream gravador = new ObjectOutputStream(arquivo);
            gravador.writeObject(filmes);
            gravador.close();
            arquivo.close();
            JOptionPane.showMessageDialog(this, "Filmes salvos com sucesso");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao exportar filmes.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void importarFilmes(ArrayList<Filme> filmes) {
        try {
            FileInputStream arquivo = new FileInputStream("filmes.obj");
            ObjectInputStream restaurador = new ObjectInputStream(arquivo);
            ArrayList<Filme> filmesImpo = null;
            filmesImpo = (ArrayList<Filme>) restaurador.readObject();
            restaurador.close();
            arquivo.close();
            for (int i = 0; i < filmesImpo.size(); i++) {
                filmes.add(filmesImpo.get(i));
                displayFilmes.append(filmesImpo.get(i).toString() + "\n\n");
                inputNome.addItem(filmesImpo.get(i).getNome());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Nenhum arquivo de filmes encontrado.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadFilmesFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("."));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                String line;
                boolean achou = false;
                while ((line = br.readLine()) != null && !achou) { // para cada filme encontrado no arquivo
                    String[] values = line.split(",");
                    if (values.length == 0) { // se a linha está vazia pula pra próxima
                        continue;
                    }
                    for (int i = 0; i < filmes.size(); i++) { // checar se o filme já existe
                        if (values[0].equals(filmes.get(i).getNome())) { // o filme já existe
                            System.out.println("Filme já existe: " + values[0]);
                            achou = true;
                            break;
                        }
                    }
                    if (achou == false) {
                        filmes.add(new Filme(values[0]));
                        inputNome.addItem(values[0]);
                        displayFilmes.append(filmes.get(filmes.size() - 1).toString() + "\n\n");
                    }
                    achou = false;
                }
                JOptionPane.showMessageDialog(this, "Filmes carregados com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar filmes.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class Filme implements java.io.Serializable {
    String nome;
    double nota;
    String desc;
    public Filme(String nome, double nota, String desc) {
        this.nome = nome;
        this.nota = nota;
        this.desc = desc;
    }
    public Filme(String nome) {
        this.nome = nome;
        this.nota = 0.0;
        this.desc = null;
    }
    public String getNome() {
        return nome;
    }
    public String toString() {
        if (desc == null) {
            return String.format("Nome: %s\nReview ainda não feita", nome);
        }
        return String.format("Nome: %s\nNota: %2.1f\nDescrição: %s", nome, nota, desc);
    }
}

class ExcecaoNotaInvalida extends Exception {
    public ExcecaoNotaInvalida(String mensagem) {
        super(mensagem);
    }
    public ExcecaoNotaInvalida() {
        super();
    }
}

class ExcecaoCampoVazio extends Exception {
    public ExcecaoCampoVazio(String mensagem) {
        super(mensagem);
    }
    public ExcecaoCampoVazio() {
        super();
    }
}