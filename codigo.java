import java.util.*;
import java.io.*;

// ----- Jogador (dados do jogador) -----
class Jogador {
    String nome;
    int idade;
    String posicao;
    int reputacao; // 0-100
    int drible;    // 0-100
    int passe;
    int chute;
    boolean profissional;

    public Jogador(String nome, int idade, String posicao, boolean profissional) {
        this.nome = nome;
        this.idade = idade;
        this.posicao = posicao;
        this.profissional = profissional;
        this.reputacao = profissional ? 30 : 10;
        this.drible = 20 + (int)(Math.random() * 30);
        this.passe = 20 + (int)(Math.random() * 30);
        this.chute = 20 + (int)(Math.random() * 30);
    }

    // cria o Cristiano inicial (uso pra modo roteiro)
    public static Jogador criarCristianoInicial() {
        Jogador j = new Jogador("Cristiano Ronaldo", 17, "Atacante", true);
        j.reputacao = 25;
        j.drible = 70;
        j.passe = 60;
        j.chute = 65;
        return j;
    }

    public String resumo() {
        return String.format("%s, %d anos, %s | Rep:%d Dr:%d Pa:%d Ch:%d",
            nome, idade, posicao, reputacao, drible, passe, chute);
    }
}

// ----- StoryNode com anosPassam (nó da história) -----
class StoryNode {
    String titulo;
    String texto;
    String opc1, opc2;
    int efeito1, efeito2;
    int anosPassam;

    public StoryNode(String titulo, String texto, String opc1, int e1, String opc2, int e2, int anosPassam) {
        this.titulo = titulo;
        this.texto = texto;
        this.opc1 = opc1;
        this.opc2 = opc2;
        this.efeito1 = e1;
        this.efeito2 = e2;
        this.anosPassam = anosPassam;
    }

    // aplica os efeitos simples da escolha no jogador
    public void aplicarEfeito(Jogador j, int escolha) {
        int e = (escolha == 1) ? efeito1 : efeito2;
        switch (e) {
            case 1: j.reputacao = Math.min(100, j.reputacao + 8); break;
            case 2: j.reputacao = Math.max(0, j.reputacao - 6); break;
            case 3:
                j.drible = Math.min(100, j.drible + 5);
                j.passe = Math.min(100, j.passe + 3);
                j.chute = Math.min(100, j.chute + 4);
                break;
            case 4:
                j.reputacao = Math.min(100, j.reputacao + 12);
                break;
            case 5:
                // resiliência / trabalho duro
                j.reputacao = Math.min(100, j.reputacao + 6);
                j.drible = Math.min(100, j.drible + 2);
                break;
            case 6:
                // reputação moral / ajudar família
                j.passe = Math.min(100, j.passe + 4);
                j.reputacao = Math.min(100, j.reputacao + 4);
                break;
            default: break;
        }
    }
}

// ----- Utils (funções úteis) -----
class Utils {
    public static int lerInt(Scanner ler, int min, int max) {
        while (true) {
            try {
                int x = Integer.parseInt(ler.nextLine().trim());
                if (x < min || x > max) {
                    System.out.printf("Digite um número entre %d e %d: ", min, max);
                    continue;
                }
                return x;
            } catch (Exception e) {
                System.out.print("Entrada inválida. Tenta de novo: ");
            }
        }
    }
}

// ----- Game (controle do jogo) -----
class Game {
    Scanner ler;
    List<Jogador> jogadores;
    Jogador jogadorAtual;

    public Game(Scanner ler) {
        this.ler = ler;
        jogadores = new ArrayList<>();
        carregarCristiano(); // carrega Cristiano como jogador existente
    }

    // cria o Cristiano fixo (modo roteiro)
    void carregarCristiano() {
        Jogador cristiano = Jogador.criarCristianoInicial();
        jogadores.clear();
        jogadores.add(cristiano);
    }

    // menu principal
    public void menuInicial() {
        while (true) {
            System.out.println("\n=== Rumo ao Topo ===");
            System.out.println("1 - Criar jogador novo (idade fixa: 16 ou 25)");
            System.out.println("2 - Jogar como Cristiano Ronaldo (roteiro da carreira)");
            System.out.println("3 - Sair");
            System.out.print("Escolha: ");
            int opc = Utils.lerInt(ler, 1, 3);
            if (opc == 1) criarJogador();
            else if (opc == 2) escolherCristiano();
            else {
                System.out.println("Falou! Boa sorte no caminho ao topo.");
                break;
            }
        }
    }

    // criação do jogador novo (idade limitada a 16 ou 25)
    void criarJogador() {
        System.out.print("Nome do jogador: ");
        String nome = ler.nextLine().trim();

        System.out.println("Escolha a idade inicial do jogador:");
        System.out.println("1 - 16 anos (jovem da base)");
        System.out.println("2 - 25 anos (profissional voltando de lesão)");
        System.out.print("Escolha 1 ou 2: ");
        int escolhaIdade = Utils.lerInt(ler, 1, 2);
        int idade = (escolhaIdade == 1) ? 16 : 25;

        System.out.print("Posição (Atacante/Meio-campo/Defensor/Goleiro/Lateral): ");
        String pos = ler.nextLine().trim();
        boolean prof = (idade >= 18); // 25 -> profissional; 16 -> base
        Jogador j = new Jogador(nome, idade, pos, prof);
        jogadores.add(j);
        jogadorAtual = j;
        System.out.println("\nJogador criado: " + jogadorAtual.resumo());

        // escolhe roteiro conforme idade
        if (idade == 16) {
            playNewPlayerCareer_16();
        } else {
            playNewPlayerCareer_25();
        }

        loopCarreira();
    }

    // escolher Cristiano
    void escolherCristiano() {
        jogadorAtual = jogadores.get(0);
        System.out.println("\nVocê escolheu jogar como: " + jogadorAtual.resumo());
        System.out.println("Modo roteiro: decisões baseadas em marcos reais da carreira.");
        playCristianoCareer();
    }

    // menu de carreira depois do roteiro
    void loopCarreira() {
        while (true) {
            System.out.println("\n=== Menu de Carreira ===");
            System.out.println("1 - Ver perfil");
            System.out.println("2 - Avançar temporada (simples)");
            System.out.println("3 - Voltar ao menu inicial");
            System.out.print("Escolha: ");
            int opc = Utils.lerInt(ler, 1, 3);
            if (opc == 1) System.out.println(jogadorAtual.resumo());
            else if (opc == 2) temporada();
            else break;
        }
    }

    // avanço simples de temporada
    void temporada() {
        System.out.println("\n--- Nova Temporada (simples) ---");
        jogadorAtual.idade += 1;
        System.out.println("Temporada avançada. Perfil: " + jogadorAtual.resumo());
    }

    // ---------- Roteiro do Cristiano (mantive como antes) ----------
    void playCristianoCareer() {
        List<StoryNode> roteiro = new ArrayList<>();
        roteiro.add(new StoryNode("Sporting — O jovem talento (2002)",
            "Você tem 17 anos e já mostra talento no Sporting. Os treinos são intensos e a imprensa local começa a notar seu potencial.",
            "Seguir disciplina tática e treinar finalizações extras.", 3,
            "Seguir meu estilo individual, arriscar jogadas e dribles.", 2,
            1));
        roteiro.add(new StoryNode("Oferta do Manchester United (2003)",
            "O Manchester United aparece com proposta. Sir Alex Ferguson espera profissionalismo e entrega máxima.",
            "Aceitar a transferência e me adaptar (foco no trabalho).", 4,
            "Recusar e ficar em Portugal por segurança.", 2,
            4));
        roteiro.add(new StoryNode("Partida decisiva no United — noite de herói",
            "É uma noite tensa: a equipe precisa de um gol. Você tem chance de firmar-se.",
            "Arriscar chute sozinho (alto risco/alto retorno).", 1,
            "Procurar o companheiro e garantir a jogada coletiva.", 3,
            0));
        roteiro.add(new StoryNode("Ascensão e Bola de Ouro (2007-2008)",
            "Você alcança picos de desempenho, prêmios e reconhecimento mundial.",
            "Manter rotina de treino e limitar distrações.", 1,
            "Aproveitar contratos e expandir imagem imediatamente.", 2,
            2));
        roteiro.add(new StoryNode("Mudança para Real Madrid (2009)",
            "A transferência recorde para o Real Madrid traz pressão por gols e títulos.",
            "Trabalhar dia a dia para bater recordes.", 4,
            "Focar também na vida social e parcerias.", 2,
            1));
        roteiro.add(new StoryNode("Real Madrid x Juventus — bicicleta",
            "Cruzamento perfeito, a bola sobe: tentar a bicicleta ou optar por segurança?",
            "Tentar bicicleta (alto risco/alta glória).", 4,
            "Finalizar seguro ou tocar.", 1,
            0));
        roteiro.add(new StoryNode("Consolidação em Madrid — recordes",
            "Sua carreira no Real segue com gols e recordes.",
            "Focar no futebol e treinos.", 1,
            "Equilibrar vida pessoal e aparições públicas.", 2,
            6));
        executarRoteiroGenerico(roteiro);
    }

    // ---------- Roteiro para jogador novo começando aos 16 (base/ascensão) ----------
    void playNewPlayerCareer_16() {
        System.out.println("\n=== Roteiro: Das Ruas ao Topo (idade 16) ===");
        List<StoryNode> roteiro = new ArrayList<>();

        roteiro.add(new StoryNode(
            "A bola como única fuga (Infância)",
            "Desde pequeno, a vida foi dura. A bola remendada era seu alento. Agora, com 16 anos, a base te chama se você mostrar talento.",
            "Levantar e continuar jogando, mostrar resiliência.", 5,
            "Treinar sozinho e evitar riscos por enquanto.", 3,
            0 // já tem 16; fica no mesmo ano
        ));

        roteiro.add(new StoryNode(
            "Peneira decisiva no clube da região",
            "Um olheiro faz uma peneira. É a chance de entrar para a base de verdade.",
            "Dar tudo na peneira — arriscar para impressionar.", 4,
            "Jogar seguro e mostrar disciplina.", 3,
            0
        ));

        roteiro.add(new StoryNode(
            "Primeiro teste na base",
            "Se aprovado, treinos mais duros e pressão. Se não, treinar mais e tentar novamente.",
            "Aceitar feedback e treinar duro com o time.", 3,
            "Treinar sozinho focando em físico.", 5,
            1 // 1 ano de adaptação na base
        ));

        roteiro.add(new StoryNode(
            "Final Sub-17 — Estádio lotado",
            "Final do campeonato juvenil. Um lance pode elevar seu nome.",
            "Chutar de longe e arriscar o golaço.", 4,
            "Contribuir para o coletivo e garantir o título.", 3,
            0
        ));

        roteiro.add(new StoryNode(
            "Primeiro contrato de formação (progresso)",
            "Diretoria oferece contrato de formação/profissional juvenil. Hora de decidir seu caminho.",
            "Assinar e crescer no clube (caminho seguro).", 1,
            "Tentar impressionar clubes maiores (arriscado).", 4,
            1
        ));

        roteiro.add(new StoryNode(
            "Estreia no time profissional",
            "Você entra como substituto em jogo oficial; minutos finais para provar.",
            "Buscar protagonismo: driblar e chutar.", 4,
            "Tocar para companheiro e garantir resultado.", 3,
            0
        ));

        roteiro.add(new StoryNode(
            "Ascensão inicial — atenção nacional",
            "Jornais começam a notar você. Convites e decisões sobre imagem surgem.",
            "Focar 100% no futebol (treino e disciplina).", 1,
            "Usar a exposição para construir imagem (entrevistas).", 2,
            2
        ));

        // executa o roteiro jovem
        for (int i = 0; i < roteiro.size(); i++) {
            StoryNode no = roteiro.get(i);
            System.out.println("\n== " + no.titulo + " ==");
            for (String linha : dividirTexto(no.texto, 100)) {
                System.out.println(linha);
                pausarCurto();
            }

            System.out.println("\n1) " + no.opc1);
            System.out.println("2) " + no.opc2);
            System.out.print("Escolha (1-2): ");
            int escolha = Utils.lerInt(ler, 1, 2);

            no.aplicarEfeito(jogadorAtual, escolha);

            // se for nó de jogo/peneira/final/estreia, simulamos
            boolean isMatch = no.titulo.toLowerCase().contains("peneira") ||
                              no.titulo.toLowerCase().contains("final") ||
                              no.titulo.toLowerCase().contains("estreia") ||
                              no.titulo.toLowerCase().contains("sub-17");

            if (isMatch) {
                int habilidade = jogadorAtual.drible + jogadorAtual.chute + jogadorAtual.reputacao;
                int chanceBase = habilidade / 3;
                if (jogadorAtual.idade < 17) chanceBase -= 6; // menos experiente
                int modificador = (escolha == 1) ? 12 : 0;
                int chanceFinal = Math.max(5, Math.min(95, chanceBase + modificador));
                int sorte = (int)(Math.random() * 100);
                boolean sucesso = (sorte < chanceFinal);
                if (sucesso) {
                    System.out.println("\nMomento decisivo bem-sucedido! Ganhou destaque.");
                    jogadorAtual.reputacao = Math.min(100, jogadorAtual.reputacao + 10);
                    jogadorAtual.drible = Math.min(100, jogadorAtual.drible + 2);
                } else {
                    System.out.println("\nLance falhou — aprendizado.");
                    if (escolha == 1 && Math.random() < 0.10) {
                        System.out.println("Pequena lesão. Recuperação breve.");
                        jogadorAtual.reputacao = Math.max(0, jogadorAtual.reputacao - 5);
                        jogadorAtual.drible = Math.max(0, jogadorAtual.drible - 3);
                    } else {
                        jogadorAtual.reputacao = Math.max(0, jogadorAtual.reputacao - 2);
                    }
                }
            }

            // avança anos (se aplicável)
            jogadorAtual.idade += no.anosPassam;
            if (no.anosPassam > 0) System.out.println("\n(" + no.anosPassam + " anos se passaram) Idade: " + jogadorAtual.idade);

            System.out.println("\nEstado: " + jogadorAtual.resumo());
            pausarCurto();
        }

        System.out.println("\n=== FIM DO ROTEIRO JOVEM (16) ===");
        System.out.println("Situação atual: " + jogadorAtual.resumo());
    }

    // ---------- Roteiro para jogador novo começando aos 25 (profissional voltando de lesão) ----------
    void playNewPlayerCareer_25() {
        System.out.println("\n=== Roteiro: Retorno e Redenção (idade 25) ===");
        List<StoryNode> roteiro = new ArrayList<>();

        roteiro.add(new StoryNode(
            "Voltando de lesão — início da recuperação",
            "Você tem 25 anos, é profissional e volta de uma lesão séria que te afastou meses. Treinos de reabilitação e dúvidas rondam.",
            "Seguir o plano de reabilitação à risca (paciência).", 5,
            "Apressar retorno para não perder posição (risco).", 2,
            0
        ));

        roteiro.add(new StoryNode(
            "Primeiro treino completo após recuperação",
            "O técnico observa com atenção. Seu primeiro treino completo é uma prova de confiança.",
            "Mostrar economia de movimentos e foco na técnica (seguro).", 3,
            "Forçar intensidade para impressionar imediatamente (arriscado).", 4,
            0
        ));

        roteiro.add(new StoryNode(
            "Convite para jogo-treino (chance de provar)",
            "Você é chamado para um amistoso/teste. É a chance de mostrar que voltou ao nível.",
            "Jogar com inteligência, buscar oportunidades.", 3,
            "Ir com tudo para marcar presença (alto risco/alta recompensa).", 4,
            0
        ));

        roteiro.add(new StoryNode(
            "Oferta de renovação / proposta externa",
            "Clubes e empresários observam sua recuperação. Ofertas surgem: renovar no clube atual com segurança ou negociar com ofertas maiores.",
            "Aceitar renovação e foco na reconstrução de forma segura.", 1,
            "Negociar com clubes maiores e buscar novo contrato (arriscado).", 4,
            1
        ));

        roteiro.add(new StoryNode(
            "Partida importante após retorno — pressão máxima",
            "Você entra em jogo oficial. Torcida e imprensa olham cada movimento.",
            "Jogar seguro e apoiar o time (reduzindo risco de recaída).", 3,
            "Buscar protagonismo e marcar para provar que voltou (arriscado).", 4,
            0
        ));

        roteiro.add(new StoryNode(
            "Decisão sobre o corpo e carreira futura",
            "Após a temporada de retorno, decidir entre foco total na recuperação contínua (menos jogos, mais cuidado) ou forçar e aproveitar oportunidades imediatas.",
            "Priorizar saúde e carreira longa (paciência).", 1,
            "Aproveitar oportunidades agora, mesmo com risco (curto prazo).", 4,
            1
        ));

        // executar roteiro de retorno
        for (int i = 0; i < roteiro.size(); i++) {
            StoryNode no = roteiro.get(i);
            System.out.println("\n== " + no.titulo + " ==");
            for (String linha : dividirTexto(no.texto, 100)) {
                System.out.println(linha);
                pausarCurto();
            }

            System.out.println("\n1) " + no.opc1);
            System.out.println("2) " + no.opc2);
            System.out.print("Escolha (1-2): ");
            int escolha = Utils.lerInt(ler, 1, 2);

            no.aplicarEfeito(jogadorAtual, escolha);

            // simula risco de recaída/lesão quando escolhas arriscadas são feitas
            if (no.titulo.toLowerCase().contains("lesão") || no.titulo.toLowerCase().contains("retorno") || no.titulo.toLowerCase().contains("partida importante")) {
                int chanceRecaida = 8; // base 8% de recaída
                if (escolha == 2) chanceRecaida += 12; // apressar retorno aumenta risco
                int mitigacao = (jogadorAtual.passe + jogadorAtual.drible) / 40;
                chanceRecaida = Math.max(2, chanceRecaida - mitigacao);
                if (Math.random() * 100 < chanceRecaida) {
                    System.out.println("\nInfelizmente houve uma recaída. Você precisará de recuperação adicional.");
                    jogadorAtual.reputacao = Math.max(0, jogadorAtual.reputacao - 8);
                    jogadorAtual.drible = Math.max(0, jogadorAtual.drible - 6);
                } else {
                    if (escolha == 2 && Math.random() * 100 < 50) {
                        System.out.println("\nSua ousadia impressionou — destaque imediato.");
                        jogadorAtual.reputacao = Math.min(100, jogadorAtual.reputacao + 10);
                        jogadorAtual.chute = Math.min(100, jogadorAtual.chute + 3);
                    } else {
                        System.out.println("\nRecuperação/partida sem maiores problemas. Continue trabalhando.");
                        jogadorAtual.reputacao = Math.min(100, jogadorAtual.reputacao + 4);
                    }
                }
            }

            // avança anos
            jogadorAtual.idade += no.anosPassam;
            if (no.anosPassam > 0) System.out.println("\n(" + no.anosPassam + " anos se passaram) Idade: " + jogadorAtual.idade);

            System.out.println("\nEstado: " + jogadorAtual.resumo());
            pausarCurto();
        }

        System.out.println("\n=== FIM DO ROTEIRO DE RETORNO (25) ===");
        System.out.println("Situação atual: " + jogadorAtual.resumo());
    }

    // ---------- Função auxiliar usada por roteiros do Cristiano ----------
    void executarRoteiroGenerico(List<StoryNode> roteiro) {
        for (int idx = 0; idx < roteiro.size(); idx++) {
            StoryNode no = roteiro.get(idx);
            System.out.println("\n== " + no.titulo + " ==");
            for (String linha : dividirTexto(no.texto, 100)) {
                System.out.println(linha);
                pausarCurto();
            }

            System.out.println("\n1) " + no.opc1);
            System.out.println("2) " + no.opc2);
            System.out.print("Escolha (1-2): ");
            int escolha = Utils.lerInt(ler, 1, 2);

            no.aplicarEfeito(jogadorAtual, escolha);

            // verifica se o nó é do tipo partida/momento histórico pra simular ação
            boolean isMatch = no.titulo.toLowerCase().contains("bicicleta") ||
                              no.titulo.toLowerCase().contains("partida") ||
                              no.titulo.toLowerCase().contains("clásico") ||
                              no.titulo.toLowerCase().contains("champions") ||
                              no.titulo.toLowerCase().contains("hat-trick") ||
                              no.titulo.toLowerCase().contains("falta");

            if (isMatch) {
                int habilidade = jogadorAtual.drible + jogadorAtual.chute + jogadorAtual.reputacao;
                int chanceBase = habilidade / 3;
                int modificador = (escolha == 1) ? 15 : 0;
                if (no.titulo.toLowerCase().contains("bicicleta") && escolha == 1) {
                    chanceBase -= 12;
                    modificador += 25;
                }
                int chanceFinal = Math.max(5, Math.min(95, chanceBase + modificador));
                int sorte = (int)(Math.random() * 100);
                boolean sucesso = (sorte < chanceFinal);

                if (sucesso) {
                    System.out.println("\nMomento histórico! A jogada foi um sucesso.");
                    jogadorAtual.reputacao = Math.min(100, jogadorAtual.reputacao + 10);
                    jogadorAtual.chute = Math.min(100, jogadorAtual.chute + 3);
                    jogadorAtual.drible = Math.min(100, jogadorAtual.drible + 2);
                } else {
                    System.out.println("\nA jogada falhou. Aprendizado e consequências.");
                    if (escolha == 1 && Math.random() < 0.12) {
                        System.out.println("Sofreu uma pequena lesão no lance.");
                        jogadorAtual.reputacao = Math.max(0, jogadorAtual.reputacao - 6);
                        jogadorAtual.drible = Math.max(0, jogadorAtual.drible - 5);
                    } else {
                        jogadorAtual.reputacao = Math.max(0, jogadorAtual.reputacao - 3);
                    }
                }
            }

            // Avança anos
            jogadorAtual.idade += no.anosPassam;
            if (no.anosPassam > 0) {
                System.out.println("\n(" + no.anosPassam + " anos se passaram) Idade agora: " + jogadorAtual.idade);
            }

            System.out.println("\nResultado: " + jogadorAtual.resumo());
            pausarCurto();
        }
    }

    // auxiliares pra quebrar texto e dar uma pausa dramática
    String[] dividirTexto(String texto, int maxChars) {
        List<String> partes = new ArrayList<>();
        int i = 0;
        while (i < texto.length()) {
            int end = Math.min(i + maxChars, texto.length());
            if (end < texto.length()) {
                int esp = texto.lastIndexOf(' ', end);
                if (esp > i) end = esp;
            }
            partes.add(texto.substring(i, end));
            i = end + 1;
        }
        return partes.toArray(new String[0]);
    }

    void pausarCurto() {
        try { Thread.sleep(220); } catch (Exception e) {}
    }
}

// ----- Main -----
public class Main {
    public static void main(String[] args) {
        Scanner ler = new Scanner(System.in);
        Game game = new Game(ler);
        game.menuInicial();
        ler.close();
    }
}
