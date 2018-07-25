package projects.tcc.simulation.algorithms.genetic;

import projects.tcc.simulation.graph.GraphHolder;
import projects.tcc.simulation.rssf.Environment;
import projects.tcc.simulation.rssf.SensorCollection;
import projects.tcc.simulation.rssf.SensorNetwork;
import projects.tcc.simulation.rssf.sensor.Sensor;
import projects.tcc.simulation.rssf.sensor.Sink;

import java.util.ArrayList;
import java.util.List;

public class AG_Estatico_MO_arq {

    public static boolean[] resolveAG_Estatico_MO(int numeroGeracoes, int tamanhoPopulacao, double txCruzamento, double txMutacao) throws Exception {

        List<Sensor> listSensensores = new ArrayList<>(SensorCollection.getAvailableSensors().values());

        GraphHolder.update();
        new ArrayList<>(SensorCollection.getActiveSensors().values()).forEach(Sensor::isConnectable);

        List<Cromossomo> vMelhorPareto;

        int numGer = 0;
        double medFitness = 0;
        int numSA = 0;

        int vNumBits = SensorCollection.getAllSensorsAndSinks().size() - SensorCollection.getSinks().size();
        long[] vetIdsSensDisp = SensorCollection.getAvailableSensors().keySet().stream().mapToLong(Long::longValue).toArray();

        Populacao popCromo = new Populacao(tamanhoPopulacao, vNumBits, vetIdsSensDisp, txCruzamento);

        double raioSens = listSensensores.get(0).getSensorRadius();

        popCromo.startPop(Environment.getArea(), raioSens, Environment.getCoverageFactor());

        calculaFuncaoObjetivo(popCromo.getPopCromossomo());

        calculaFuncaoObjetivo2(popCromo.getPopCromossomo());

        limpaPareto(popCromo.getPopCromossomo());
        gerarParetos(popCromo.getPopCromossomo());
        popCromo.calcularFO_MO_1();

        //			popCromo.ordenaParetos();

        popCromo.setMelhorPareto();
        vMelhorPareto = popCromo.copyMelhorPareto();

        int cont = 0;
        //			while (contConv < 10)
        for (int cNumeroGeracoes = 0; cNumeroGeracoes < numeroGeracoes; cNumeroGeracoes++) {

            popCromo.realizaCasamento();
            popCromo.realizaMutacao();

            calculaFuncaoObjetivo(popCromo.getPopCromossomo());
            calculaFuncaoObjetivo2(popCromo.getPopCromossomo());

            limpaPareto(popCromo.getPopCromossomo());
            gerarParetos(popCromo.getPopCromossomo());


            //popCromo.ordenaParetos(); // Ordena os Paretos!

            popCromo.setMelhorPareto();
            popCromo.incrementarValorPareto();
            popCromo.inserirPopArq(vMelhorPareto);
            popCromo.calcularFO_MO_1();
            //conjMelhorPareto = popCromo.getMelhorPareto();

            //saidaMO.geraArqSaidaMO3("paretoOtimo"+cont+".txt", conjMelhorPareto);
            //saidaMO.geraArqSaidaPop("popTotal"+cont+".txt", popCromo.getPopCromossomo());

            popCromo.setPopCromossomo(selecaoRoleta(popCromo.getPopCromossomo(), popCromo.getTamPopOrig()));
            //operAG.selecaoRoleta(popCromo, popCromo.getTamPopOrig(), vNumBits);

            //				System.out.println("Saiu = " + cont);
            //				System.out.println("size = " + popCromo.getPopCromossomo().size());
            //				System.out.println("Entrou no elitismo = " + cont);
            boolean testeConv = elitismoMelhorPareto(popCromo, vMelhorPareto);

            if (!testeConv) {
            }

            //				System.out.println("size = " + popCromo.getPopCromossomo().size());
            //				System.out.println("contConv = " + contConv);
            //				System.out.println("Saiu do elitismo = " + cont);

            popCromo.ajustarValorPareto();
            popCromo.setMelhorPareto();
            vMelhorPareto = popCromo.copyMelhorPareto();

            cont++;

        }

        //imprimir teste
        limpaPareto(popCromo.getPopCromossomo());
        gerarParetos(popCromo.getPopCromossomo());
        popCromo.calcularFO_MO_3();
        popCromo.ordenaF1(); //ordena pela fitness
        popCromo.setMelhorPareto();
        //			popCromo.ordenaParetos();
        //			for (int k = 0; k < popCromo.getPopSize(); k++) {

        //			System.out.println(k + " pareto = " + popCromo.getPopCromossomo().get(k).getIdPareto()
        //			+ "\t fitnessPareto = " + popCromo.getPopCromossomo().get(k).getFitnessMO());

        //			}

        vMelhorPareto = popCromo.copyMelhorPareto();
        //Separando os melhores paretos para uma m�dia.

        List<Cromossomo> conjMelhorPareto = new ArrayList<>(vMelhorPareto);


        //			System.out.println("numero de geracaoes = " + cont);


        //imprimindo a Popula��o resultante.
        //popCromo.printPop();

        //Escolher um resultado no pareto
        //			popCromo.ordenaFitness1(); //ordena pela fitness

        popCromo.setMelhorCromo(decSolPareto(conjMelhorPareto, listSensensores));

        //pegando o melhor indiv�duo (melhor Cromossomo)
        Cromossomo vMelhorCromossomo = popCromo.getMelhorIndv();
        //vMelhorCromossomo = popCromo.getMelhorIndvRol();

		/*System.out.println("\n Solucao do AG:");
			popCromo.printMelhorIndv();*/

        //			mAmbiente.incrementaCont();
        //			int contAG = mAmbiente.getCont();

        //output.geraArqSaidaMO("paretoOtimo.txt", conjMelhorPareto);
        //output.geraArqSaidaPopMO("popTotal"+contAG+".txt", popCromo.getPopCromossomo());
        //output.geraArqSaidaMpPareto("melhorPonto.txt", popCromo.getMelhorIndv());

		/*cont--;
			output.geraArqSaidaMpPareto("melhorPonto"+cont+".txt", popCromo.getMelhorIndv());
		 */


		/*System.out.println("\nNumero de Ativos Gen�ico: " + 
					vMelhorCromossomo.getNumeroAtivos());
		 */

        //Fazendo a m�dia dos resultados...


        limpaPareto(conjMelhorPareto);
        gerarParetos(conjMelhorPareto);
        popCromo.calcularFO_MO_3();
        //popCromo.ordenaParetos();


        //geraArqSaidaMO("testeMO.out", conjMelhorPareto);/*
        //geraArqSaidaMO2("testeMO2.out", conjMelhorPareto);*/
        //geraArqSaidaMpPareto("pePareto.out", popCromo.getMelhorIndv());


        return popCromo.getMelhorIndv().getVetorBoolean();

    }


    // Prepara��o para uma nova chamada do AG_Estatico
	/*public static int [] novaSolucao (Ambiente mAmbiente, Saidas output) throws Exception{

		ArrayList<Sensor> popTotal;
		ArrayList<Sensor> popAG;

		popTotal = rede.getPopSensores();

		popAG = new ArrayList<Sensor>();


		for (int i = 0; i < popTotal.size(); i++) {    		
			if (!(popTotal.get(i).estaFalha()))    			
				popAG.add(popTotal.get(i));    		    		
		}


		int [] vetBits_popAG  = new int[popAG.size()-1]; //-1 pois o Sink n�o entra
		int [] vetBits_return = new int[rede.getNumSensInicial()];

		for(int i = 0; i < rede.getNumSensInicial(); i++)
			vetBits_return[i] = 0;


		if (popAG.size() > 3) {
			vetBits_popAG = resolveAG_Estatico_MO (rede, mAmbiente, popAG, null, output);
		}

		else
			return vetBits_return;



		// Refazendo o vetBits
		for (int i = 0; i < vetBits_popAG.length; i++) {

			if (vetBits_popAG[i] == 1) {

				int idSens = popAG.get(i).getId();

				vetBits_return[idSens] = 1;

			}

		}

		return vetBits_return;


	}*/


    /*evaluates objective function for each chromossome*/
    static void calculaFuncaoObjetivo(List<Cromossomo> pCromossomos) {

        double penAtiv = SensorCollection.getAvailableSensors().values().stream()
                .mapToDouble(s -> s.getActivationPower() + s.getMaintenancePower())
                .findAny()
                .orElse(0);

        int penNCob = 0;//100000 utilizado no mono-objetivo;

        for (Cromossomo indv : pCromossomos) {
            // avalia apenas quem precisa
            if (indv.isAvaliarFO()) {
                avaliarIndividuo(indv, penAtiv, penNCob);
            }
        }
    }


    public static void avaliarIndividuo(Cromossomo individuo, double penAtiv, int penNCob) {

        individuo.setNaoCobertura(Environment.getPoints().size()
                - Environment.getCoveredPoints().size());

        SensorNetwork.updateActiveSensors(individuo.getVetorBits());
        double custoCaminhoTotal = 0;
        for (Sensor sens : SensorCollection.getAvailableSensors().values()) {
            for (Sink sink : SensorCollection.getSinks().values()) {
                if (sens.isActive()) {
                    custoCaminhoTotal += sens.getGraphNodeProperties().getPathToSinkCost()
                            .getOrDefault(sink.getID(), 0.0);
                }
            }
        }

        int penCustoCaminho = 100;

        individuo.calculateFitness(penAtiv, custoCaminhoTotal * penCustoCaminho, penNCob);

    }


    /*evaluates objective function for each chromossome*/
    private static void calculaFuncaoObjetivo2(List<Cromossomo> pCromossomos) {

        int penNCob = 100000;
        double penAtiv = 100000;

        double raioSens = SensorCollection.getAvailableSensors().values().stream()
                .mapToDouble(Sensor::getSensorRadius)
                .findAny()
                .orElse(0);

        for (Cromossomo indiv : pCromossomos) {

            double vFitness2 = indiv.getFitness2();

            // avalia apenas quem precisa
            if (vFitness2 < 0) {
                indiv.calculateFitness2(raioSens, penNCob, penAtiv);
            }
        }

    }

    public static List<Cromossomo> selecaoRoleta(List<Cromossomo> popCromo, int tamPopOrig) {

        List<Cromossomo> popCromoAux = new ArrayList<>();

        double totalFitness = 0;

        for (Cromossomo aPopCromo1 : popCromo) {
            totalFitness = totalFitness + aPopCromo1.getFitnessMO();
        }

        for (int i = 0; i < tamPopOrig; i++) {

            int vRand = (int) (totalFitness * (Math.random()));

            double s = 0;

            for (Cromossomo aPopCromo : popCromo) {

                s = s + aPopCromo.getFitnessMO();

                if (s >= vRand) {
                    Cromossomo cromoCorrente = aPopCromo;
                    Cromossomo cromoClone = new Cromossomo(cromoCorrente);
                    popCromoAux.add(cromoClone);
                    break;
                }


            }

        }

        return popCromoAux;
    }


    public static int[][] gerarMatDomi(List<Cromossomo> popCromo) {

        int tamPopCromo = popCromo.size();
        int[][] matDomin = new int[tamPopCromo][tamPopCromo];

        for (int j = 0; j < tamPopCromo; j++) {

            Cromossomo cromoA = popCromo.get(j);

            for (int i = 0; i < tamPopCromo; i++) {

                Cromossomo cromoB = popCromo.get(i);

                if (j != i) {
                    //Marca com 1 todos os que s�o dominados pela coluna J!!!
                    if (testeDominancia(cromoA, cromoB)) {
                        matDomin[i][j] = 1;
                    } else
                        matDomin[i][j] = 0;

                } else {
                    matDomin[i][j] = 0;
                }

            }

        }

        return matDomin;

    }


    //Testando se o CromoA domina o CromoB
    public static boolean testeDominancia(Cromossomo cromoA, Cromossomo cromoB) {

        double fitnA1 = cromoA.getFitness();
        double fitnA2 = cromoA.getFitness2();

        double fitnB1 = cromoB.getFitness();
        double fitnB2 = cromoB.getFitness2();

        if (fitnB1 > fitnA1 && fitnB2 > fitnA2)
            return true;
        else if (fitnB1 >= fitnA1 && fitnB2 > fitnA2)
            return true;
        else if (fitnB1 > fitnA1 && fitnB2 >= fitnA2)
            return true;
        else
            return false;


    }

    public static void gerarParetos(List<Cromossomo> popCromo) {

        int tamPopCromo = popCromo.size();
        int[][] matDomin;


        matDomin = gerarMatDomi(popCromo);


        //separando os paretos
        int numPareto = 0;
        int numCromoPareto = 0;

        //while (numCromoPareto < tamPop) {
        while (numCromoPareto != popCromo.size()) {


            numPareto++;

            for (int i = 0; i < tamPopCromo; i++) {

                //testando se o cromossomo j� est� no pareto
                if (popCromo.get(i).getPresPareto())
                    continue;

                boolean testePareto = true;

                for (int j = 0; j < tamPopCromo; j++) {

                    if (matDomin[i][j] == 1) {
                        testePareto = false;
                        break;
                    }
                }


                if (testePareto) {
                    popCromo.get(i).setIdPareto(numPareto);
                    popCromo.get(i).setPresPareto(true);
                    //popCromo.get(i).setFitnessMO((double) numPareto);
                }

            }


            //tirando os pontos q foram para o pareto;
            //for (int i = 0; i < paretoCorrente.size(); i++) {
            for (int i = 0; i < popCromo.size(); i++) {

                //int ind = paretoCorrente.get(i);
                if (popCromo.get(i).getIdPareto() == numPareto) {

                    //contando pontos q est�o indo para um pareto
                    numCromoPareto++;

                    for (int j = 0; j < tamPopCromo; j++) {

                        matDomin[i][j] = -1;
                        matDomin[j][i] = -1;
                    }
                }

            }


            //			System.out.println("numCromoPareto = " + numCromoPareto);
            //			System.out.println("tamPopCromo = " + tamPopCromo);

            //			for (int i = 0; i < tamPopCromo; i++) {
            //			for (int j = 0; j < tamPopCromo; j++) {
            //			System.out.print(matDomin[i][j] + " ");
            //			}
            //			System.out.println("\t" + popCromo.get(i).getPresPareto()
            //			+ "\t" + popCromo.get(i).getIdPareto());
            //			}

        }

    }

    public static void limpaPareto(List<Cromossomo> popCromo) {

        for (Cromossomo aPopCromo : popCromo) {
            aPopCromo.setIdPareto(Integer.MAX_VALUE);
            aPopCromo.setPresPareto(false);
        }

    }


    public static boolean elitismoMelhorPareto(Populacao popCromo,
                                               List<Cromossomo> melhorPareto) {

        List<Cromossomo> paretoPopCorrente = popCromo.getMelhorPareto();

        //		System.out.println("melhorPareto.size() ==" + melhorPareto.size() );
        //		System.out.println("paretoPopCorrente.size() ==" + paretoPopCorrente.size() );

        int contIndvPareto = 0;
        for (Cromossomo isMelhorPareto : melhorPareto) {

            boolean isDominado = false;

            for (Cromossomo isParetoCorrente : paretoPopCorrente) {

                //Verificando se eh dominado e se eh o mesmo ponto
                if (testeDominancia(isParetoCorrente, isMelhorPareto) ||
                        (isMelhorPareto.getFitness() == isParetoCorrente.getFitness() &&
                                isMelhorPareto.getFitness2() == isParetoCorrente.getFitness2())) {

                    isDominado = true;
                    break;
                }

            }

            //insere o cromossomo guardado caso ele ainda seja pareto
            if (!isDominado) {

                popCromo.getPopCromossomo().add(isMelhorPareto);
                contIndvPareto++;

				/*				int vRand = (int)(popCromo.getPopCromossomo().size()*(Math.random()));
				while (popCromo.getPopCromossomo().get(vRand).getIdPareto() < 2){
					vRand = (int)(popCromo.getPopCromossomo().size()*(Math.random()));
				}
				popCromo.getPopCromossomo().remove(vRand);
				popCromo.getPopCromossomo().add(isMelhorPareto);
				contIndvPareto++;
				 */
            }

        }

        return contIndvPareto != melhorPareto.size();

    }

    public static boolean elitismoMelhorPareto_arq(Populacao popCromo,
                                                   List<Cromossomo> melhorPareto) {

        List<Cromossomo> paretoPopCorrente = popCromo.getMelhorPareto();

        //		System.out.println("melhorPareto.size() ==" + melhorPareto.size() );
        //		System.out.println("paretoPopCorrente.size() ==" + paretoPopCorrente.size() );

        int contIndvPareto = 0;
        for (int i = 0; i < melhorPareto.size(); i++) {

            Cromossomo isMelhorPareto = melhorPareto.get(i);

            boolean isDominado = false;

            for (Cromossomo isParetoCorrente : paretoPopCorrente) {

                //Verificando se eh dominado e se eh o mesmo ponto
                if (testeDominancia(isParetoCorrente, isMelhorPareto) ||
                        (isMelhorPareto.getFitness() == isParetoCorrente.getFitness() &&
                                isMelhorPareto.getFitness2() == isParetoCorrente.getFitness2())) {

                    isDominado = true;
                    melhorPareto.remove(i);
                    break;
                }

            }

            //insere o cromossomo guardado caso ele ainda seja pareto
            if (!isDominado) {

                popCromo.getPopCromossomo().add(isMelhorPareto);
                contIndvPareto++;

            }

        }

        return contIndvPareto != melhorPareto.size();

    }

    public static void ajusteCoord(List<Cromossomo> conjSolPareto, int fatCob, int fatEn) {

        //achando os limites
        double limX = conjSolPareto.get(conjSolPareto.size() - 1).getFitness();
        double limY = conjSolPareto.get(0).getFitness2();

        for (Cromossomo aConjSolPareto : conjSolPareto) {

            double x = aConjSolPareto.getFitness();
            double y = aConjSolPareto.getFitness2();

            aConjSolPareto.setFitness(fatEn * x / limX);
            aConjSolPareto.setFitness2(fatCob * y / limY);

        }

        double desX = conjSolPareto.get(conjSolPareto.size() - 1).getFitness2();
        double desY = conjSolPareto.get(0).getFitness();

        for (Cromossomo aConjSolPareto : conjSolPareto) {

            double x = aConjSolPareto.getFitness();
            double y = aConjSolPareto.getFitness2();

            aConjSolPareto.setFitness(x - desY);
            aConjSolPareto.setFitness2(y - desX);

        }

    }

    public static Cromossomo decSolPareto(List<Cromossomo> conjSolPareto,
                                          List<Sensor> popSensores) throws Exception {

        //Ajuste de coordenadas.

        int index;
        //ajusteCoord(conjSolPareto, fatCob, fatEn);

        index = conjSolPareto.size() - 1;

        double fator = 1.0 - Environment.getCoverageFactor(); //% n�o cobertura
        for (int i = 0; i < conjSolPareto.size(); i++) {
            Cromossomo cromoAux = conjSolPareto.get(i);
            int pontosDescobertos = cromoAux.getNaoCobertura();

            if (pontosDescobertos == 0) {
                index = i;
                break;
            } else if ((double) pontosDescobertos / Environment.getPoints().size() <= fator) {
                index = i;
                fator = (double) pontosDescobertos / Environment.getPoints().size();
                break;
            }

            //System.out.println("\n Ponto = " + i);
            //System.out.println("pontosDescobertos = " + pontosDescobertos);

        }

        return conjSolPareto.get(index);

    }

}
