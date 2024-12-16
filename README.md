Am vrut sa imi ofer posibilitatea sa modific cat mai usor codul, deci am creat cat mai multe clase folosind doua DP -uri: Command si Strategy;
Command l-am folosit pentru comenzile de intrare (printUsers, addAccount, createCard, etc..). Am un Invoker care primeste comanda de la Client(Main) si ii apeleaza metoda execute. Am creat clase concrete pentru fiecare comanda. Acestea implementeaza interfata Command si tin loc de Receiver. 

Strategy l-am folosit pentru implementarea metodei de plata: plata online cu cardul sau plata prin transfer bancar. Cele doua clase concrete implementeaza interfata PayStrategy cu metoda generica pay();
Clasa PaymentContext are atributia de a executa plata prin metoda ceruta.

Comanda printTransactions este destul de complexa, deci eu am considerat clase specifice pentru ficare tranzactie. Acestea implementeaza o interfata comuna Tranzaction. Am procedat astfel pentru ca fiecare tranzactie avea campuri si afisare diferita, singurele care se pastrau comune fiind timestamp si description. La crearea instantelor toate sunt de tip Transaction pentru a putea sa le adaug intr-un Array pentru afisare. (Transaction transaction = new NewCardTransaction(..)). Am un Array de tranzactii pentru fiecare utilizator dar si pentru fiecare cont. La generarea rapoartelor tranzactiile erau specifice unui cont intr-un anumit interval de timp, iar la printTransactions tranzactiile erau specifice tuturor conturilor unui utilizator. 

Clasa abstracta JsonOutput afiseaza in format Json output-ul. Este abstracta pentru ca nu am vrut sa poata fi instantiata, vrand sa ii apelez metodele cu ajutorul numelui ei. 
