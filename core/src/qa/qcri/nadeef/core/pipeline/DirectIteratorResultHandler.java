/*
 * QCRI, NADEEF LICENSE
 * NADEEF is an extensible, generalized and easy-to-deploy data cleaning platform built at QCRI.
 * NADEEF means "Clean" in Arabic
 *
 * Copyright (c) 2011-2013, Qatar Foundation for Education, Science and Community Development (on
 * behalf of Qatar Computing Research Institute) having its principle place of business in Doha,
 * Qatar with the registered address P.O box 5825 Doha, Qatar (hereinafter referred to as "QCRI")
 *
 * NADEEF has patent pending nevertheless the following is granted.
 * NADEEF is released under the terms of the MIT License, (http://opensource.org/licenses/MIT).
 */

package qa.qcri.nadeef.core.pipeline;

import qa.qcri.nadeef.core.datamodel.IteratorResultHandler;
import qa.qcri.nadeef.core.datamodel.Rule;
import qa.qcri.nadeef.core.datamodel.Violation;
import qa.qcri.nadeef.tools.Tracer;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

public class DirectIteratorResultHandler implements IteratorResultHandler {
    private Rule rule;
    private LinkedBlockingQueue<Violation> violations;

    public DirectIteratorResultHandler(Rule rule, LinkedBlockingQueue<Violation> violations) {
        this.rule = rule;
        this.violations = violations;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void handle(T item) {
        Tracer tracer = Tracer.getTracer(DirectIteratorResultHandler.class);
        try {
            Collection<Violation> detectResult = rule.detect(item);
            violations.addAll(detectResult);
        } catch (Exception ex) {
            tracer.err("Exception during detection", ex);
        }
    }
}
