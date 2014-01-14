package kiss.lang.expression;

import clojure.lang.IPersistentMap;
import clojure.lang.IPersistentSet;
import kiss.lang.Environment;
import kiss.lang.Expression;
import kiss.lang.Type;
import kiss.lang.impl.KissUtils;

/**
 * Expression for a standard "if" conditional
 * 
 * @author Mike
 */
public class If extends Expression {
	private final Type type;
	private final Expression cond;
	private final Expression doThen;
	private final Expression doElse;
	
	private If(Expression cond, Expression doThen, Expression doElse) {
		this.cond=cond;
		this.doThen=doThen;
		this.doElse=doElse;
		this.type=doThen.getType().union(doElse.getType());
	}
	
	public Expression optimise() {
		Expression cond=this.cond.optimise();
		Expression doThen=this.doThen.optimise();
		Expression doElse=this.doElse.optimise();
		Type t=cond.getType();
		if (cond.isConstant()) {
			return (KissUtils.truthy(cond.eval()))?doThen:doElse;
		} 
		if (cond.isPure()) {
			if (t.cannotBeFalsey()) return doThen;
			if (t.cannotBeTruthy()) return doElse;
		}
		if ((cond==this.cond)&&(doThen==this.doThen)&&(doElse==this.doElse)) return this;
		return new If(cond,doThen,doElse);
	}
	
	public static Expression create(Expression cond,Expression doThen, Expression doElse) {
		return new If(cond,doThen,doElse);
	}
	
	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Expression specialise(Type type) {
		Expression newThen=doThen.specialise(type);
		Expression newElse=doElse.specialise(type);
		if ((newThen==null)||(newElse==null)) return null;
		if ((doThen==newThen)||(doElse==newElse)) return this;
		return new If(cond,newThen,newElse);
	}
	
	@Override
	public Expression substitute(IPersistentMap bindings) {
		Expression ncond=cond.substitute(bindings);
		if (ncond==null) return null;
		Expression nthen=doThen.substitute(bindings);
		if (nthen==null) return null;
		Expression nelse=doElse.substitute(bindings);
		if (nelse==null) return null;
		
		if ((ncond==cond)&&(nthen==doThen)&&(nelse==doElse)) return this;
		return create(ncond,nthen,nelse);
	}

	@Override
	public Environment compute(Environment d, IPersistentMap bindings) {
		d=cond.compute(d, bindings);
		if (KissUtils.truthy(d.getResult())) {
			return doThen.compute(d, bindings);
		} else {
			return doElse.compute(d, bindings);
		}
	}
	
	@Override
	public IPersistentSet getFreeSymbols(IPersistentSet s) {
		s=cond.getFreeSymbols(s);
		s=doThen.getFreeSymbols(s);
		s=doElse.getFreeSymbols(s);
		return s;
	}

	@Override
	public void validate() {
		// OK?
	}

}
