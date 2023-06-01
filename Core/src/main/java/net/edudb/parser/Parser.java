/*
 *
 * EduDB is made available under the OSI-approved MIT license.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * /
 */

package net.edudb.parser;

import adipe.translate.TranslationException;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import net.edudb.Response;
import net.edudb.ResponseStatus;
import net.edudb.engine.Config;
import net.edudb.engine.DatabaseEngine;
import net.edudb.plan.PlanFactory;
import net.edudb.query.QueryTree;
import net.edudb.relation.Relation;
import net.edudb.relation.RelationIterator;
import net.edudb.statement.SQLStatement;
import net.edudb.statement.SQLStatementFactory;
import net.edudb.statement.SQLStatementType;
import net.edudb.transcation.SynchronizedTransaction;
import net.edudb.transcation.TransactionManager;

/**
 * The SQL parser.
 *
 * @author Ahmed Abdul Badie
 */
public class Parser {
    /**
     * @uml.property name="sqlparser"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    TGSqlParser sqlparser;
    /**
     * @uml.property name="planFactory"
     * @uml.associationEnd multiplicity="(1 1)"
     */
    PlanFactory planFactory;

    public Parser() {
        sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        planFactory = new PlanFactory();
    }

    /**
     * Parses and executes an SQL query by encapsulating it inside a
     * synchronized transaction.
     *
     * @param strSQL The SQL string to parse.
     * @throws TranslationException
     */
    public Response parseSQL(String strSQL) {
        sqlparser.setSqltext(strSQL.replace(";", ""));
        int ret = sqlparser.parse();
        if (ret == 0) {
            SQLStatementFactory statementFactory = new SQLStatementFactory();
            SQLStatement statement = statementFactory.makeSQLStatement(sqlparser.sqlstatements.get(0));

            if (statement == null) {
                return new Response("Unsupported SQL statement", ResponseStatus.ERROR);
            }

            QueryTree plan;
            try {
                plan = planFactory.makePlan(statement);
            } catch (TranslationException e) {
                return new Response(e.getMessage(), ResponseStatus.ERROR);
            }

            SynchronizedTransaction transaction = new SynchronizedTransaction(plan);
            Relation relation = TransactionManager.getInstance().execute(transaction);

            Response response = new Response("Executed successfully", ResponseStatus.OK);

            if (statement.statementType() == SQLStatementType.SQLSelectStatement) {
                RelationIterator iterator = relation.getIterator();
                iterator.releasePagesLock(); // so another threads can use these pages
                DatabaseEngine.getInstance().addResultSet(Config.getCurrentWorkspace(), Config.getCurrentDatabaseName(),
                        iterator);

                response.setResultSetId(iterator.getId());
            }

            return response;
        } else {
            return new Response(sqlparser.getErrormessage(), ResponseStatus.ERROR);
        }
    }
}
