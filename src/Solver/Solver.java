package Solver;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

class Solver {

    static void solveModel(int n, int m, double[] c, double[][] A,
                           double[] b, TextField tf_komCel, TextField tf_ilSztuk1,
                           TextField tf_ilSztuk2, TextField tf_ilSztuk3, TextField tf_ilSztuk4) {
        try {
            int tmp = m;
            m = n;
            n = tmp;

            // Instantiate an empty model
            IloCplex model = new IloCplex();

            // Define an array of decision variables
            IloNumVar[] x = new IloNumVar[n];
            for (int i = 0; i < n; i++) {
                // Define each variable's range from 0 to +Infinity
                x[i] = model.numVar(0, Double.MAX_VALUE);
            }

            // Define the objective function
            IloLinearNumExpr obj = model.linearNumExpr();
            // Define and add expressions for objective function
            for (int i = 0; i < n; i++) {
                obj.addTerm(c[i], x[i]);
            }
            // Define a minimization problem
            //model.addMinimize(obj);
            model.addMaximize(obj);

            // Define the constraints
            // Create a list of constraints
            List<IloRange> constraints = new ArrayList<IloRange>();

            for (int i = 0; i < m; i++) { // for each constraint
                IloLinearNumExpr constraint = model.linearNumExpr();
                for (int j = 0; j < n; j++) { // for each variable
                    constraint.addTerm(A[i][j], x[j]);
                }
                //constraints.add(model.addGe(constraint, b[i])); //=>
                constraints.add(model.addLe(constraint, b[i])); // <=
            }

            // Suppress the auxiliary output printout
            model.setParam(IloCplex.IntParam.SimDisplay, 0);
            // Solve the model
            boolean isSolved = model.solve();
            if (isSolved) {
                tf_komCel.setText(String.valueOf(model.getObjValue()));

                // Print out the objective function
                System.out.println("obj_val = " + model.getObjValue());
                System.out.println();

                //Print out the decision variables
                if (n == 1) {
                    tf_ilSztuk1.setText(String.valueOf(model.getValue(x[0])));
                } else if (n == 2) {
                    tf_ilSztuk1.setText(String.valueOf(model.getValue(x[0])));
                    tf_ilSztuk2.setText(String.valueOf(model.getValue(x[1])));
                } else if (n == 3) {
                    tf_ilSztuk1.setText(String.valueOf(model.getValue(x[0])));
                    tf_ilSztuk2.setText(String.valueOf(model.getValue(x[1])));
                    tf_ilSztuk3.setText(String.valueOf(model.getValue(x[2])));
                } else if (n == 4) {
                    tf_ilSztuk1.setText(String.valueOf(model.getValue(x[0])));
                    tf_ilSztuk2.setText(String.valueOf(model.getValue(x[1])));
                    tf_ilSztuk3.setText(String.valueOf(model.getValue(x[2])));
                    tf_ilSztuk4.setText(String.valueOf(model.getValue(x[3])));
                }

                for (int i = 0; i < n; i++) { // for each variable
                    System.out.println("x[" + (i + 1) + "] = " + model.getValue(x[i]));
                    System.out.println("Reduced cost = " + model.getReducedCost(x[i]));
                    System.out.println();
                }
                // Check for binding/non-binding constraints
                for (int i = 0; i < constraints.size(); i++) { // for each constraint
                    double slack = model.getSlack(constraints.get(i));
                    double dual = model.getDual(constraints.get(i));
                    if (slack != 0) {
                        System.out.println("Constraint " + (i + 1) + " is non-binding.");
                    } else {
                        System.out.println("Constraint " + (i + 1) + " is binding.");
                    }
                    System.out.println("Shadow price = " + dual);
                    System.out.println();
                }
            } else {
                System.out.println("Model not solved :(");
            }
        } catch (IloException ex) {
            ex.printStackTrace();
        }
    }
}