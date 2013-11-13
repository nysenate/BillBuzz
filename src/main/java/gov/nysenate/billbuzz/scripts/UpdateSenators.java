package gov.nysenate.billbuzz.scripts;

import gov.nysenate.billbuzz.model.BillBuzzSenator;
import gov.nysenate.billbuzz.util.Application;
import gov.nysenate.billbuzz.util.BillBuzzDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Checks the OpenLegislation senators listing for the current session for new senators.
 *
 * @author GraylinKim
 *
 */
public class UpdateSenators extends BaseScript
{
    private static final Logger logger = Logger.getLogger(UpdateSenators.class);

    public static void main(String[] args) throws Exception
    {
        new UpdateSenators().run(args);
    }

    public Options getOptions()
    {
        Options options = new Options();
        options.addOption("y", "year", true, "The session year in YYYY format to update senator info on.");

        return options;
    }

    public void execute(CommandLine opts) throws IOException, SQLException
    {
        BillBuzzDAO dao = new BillBuzzDAO();
        int session = Integer.parseInt(opts.getOptionValue("year", String.valueOf(dao.getSession())));
        logger.info("Updating senators for session: "+session);

        Response response = Request.Get("http://open.nysenate.gov/legislation/senators/"+session+".json").execute();
        JsonNode root = new ObjectMapper().readTree(response.returnContent().asString());
        Iterator<JsonNode> senatorIterator = root.getElements();

        while (senatorIterator.hasNext()) {
            JsonNode senatorNode = senatorIterator.next();
            String name = senatorNode.get("name").asText();
            String shortName = senatorNode.get("shortName").asText();
            logger.info("Updating "+name+": "+shortName+"-"+session);
            QueryRunner runner = new QueryRunner(Application.getDB().getDataSource());
            BillBuzzSenator senator = runner.query("SELECT * FROM billbuzz_senator WHERE shortName=? and session=?", new BeanHandler<BillBuzzSenator>(BillBuzzSenator.class), shortName, session);
            if (senator == null) {
                senator = new BillBuzzSenator(name, shortName, session);
                runner.update("INSERT INTO billbuzz_senator (name, shortName, active, session) VALUES (?, ?, ?, ?)", senator.getName(), senator.getShortName(), senator.isActive(), senator.getSession());
                senator.setId(dao.lastInsertId(runner));
            }
        }
        logger.info("Done updating senators.");
    }
}
