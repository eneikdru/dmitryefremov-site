import os
import re
import math
from typing import Dict, List, Any, Optional

# Base directory for Barcan corpus
CORPUS_DIR = "/opt/EneikProductionSys"
PATTERNS_DIR = os.path.join(CORPUS_DIR, "docs", "philosopher-patterns")

# Attenuation factor lambda per graph hop
ATTENUATION_LAMBDA = 0.70
ACTIVATION_THRESHOLD = 0.35

# Tag definitions with keywords and semantic axes
TAG_AXES = {
    "BARCAN-TAG-00": {
        "name": "Code Guardian & Boundary Review",
        "axes": ["boundary", "review", "scope", "integration", "files", "blast-radius", "pr", "diff"],
        "summary": "Strict file scope isolation. Never modify unrelated files or schemas. Blast radius must be strictly bounded."
    },
    "BARCAN-TAG-01": {
        "name": "Actualist Object & Temporal Domain",
        "axes": ["booking", "schedule", "calendar", "time", "interval", "duration", "slot", "appointment", "overlap", "buffer", "domain", "types"],
        "summary": "Model time slots as half-open intervals [t_start, t_end + buffer). Collision occurs if max(start1, start2) < min(end1, end2)."
    },
    "BARCAN-TAG-02": {
        "name": "Rigid Designator & Nominal Reference",
        "axes": ["id", "uuid", "naming", "reference", "identifier", "api", "key", "immutable", "slug"],
        "summary": "Entity identifiers and types are nominal invariants. Identity cannot mutate across states or tool turns."
    },
    "BARCAN-TAG-03": {
        "name": "Belief Intension & Client Perception",
        "axes": ["client", "user", "ui", "ux", "feedback", "view", "notification", "format", "localization", "language"],
        "summary": "Client-facing expectations and localization constraints (e.g. Georgian phone regex, GEL currency) must be strictly validated."
    },
    "BARCAN-TAG-04": {
        "name": "Modal Quantifier & Uncertainty",
        "axes": ["concurrency", "thread", "race", "parallel", "atomic", "lock", "mutex", "simultaneous", "async"],
        "summary": "Multi-client race conditions must be serialized atomically with per-entity mutex/locks to guarantee zero double bookings."
    },
    "BARCAN-TAG-05": {
        "name": "Necessary Identity & State History",
        "axes": ["history", "replay", "audit", "log", "persistence", "event", "transaction", "record"],
        "summary": "Transactional state history must be preserved; never physically delete records without an audit trail."
    },
    "BARCAN-TAG-06": {
        "name": "Deontic Consistency & State Machine",
        "axes": ["state", "status", "transition", "lifecycle", "enum", "workflow", "order", "phase"],
        "summary": "State transitions form a strict directed acyclic graph. Exclude illegal transitions before writing to storage."
    },
    "BARCAN-TAG-07": {
        "name": "Second-Order Knowledge & Verification",
        "axes": ["test", "verify", "assert", "evidence", "check", "validation", "qa"],
        "summary": "Every claim of completion must be verified by executable falsification tests demonstrating failure on defect."
    },
    "BARCAN-TAG-08": {
        "name": "Substitutivity Salva Veritate & Contracts",
        "axes": ["contract", "schema", "interface", "dto", "substitute", "compatibility", "signature"],
        "summary": "Public API signatures, return types, and schemas must maintain backward compatibility salva veritate."
    },
    "BARCAN-TAG-09": {
        "name": "Moral Dilemma & Conflict Resolution",
        "axes": ["conflict", "dilemma", "fallback", "recovery", "priority", "resolution"],
        "summary": "When multiple constraints compete, prioritize data integrity and safety over optimistic completion."
    },
    "BARCAN-TAG-10": {
        "name": "Deontic Prohibition & Negative Gates",
        "axes": ["prohibition", "forbidden", "cancel", "prevent", "guard", "reject", "exclude"],
        "summary": "Negative deontic gate: completed appointments or settled payments can NEVER transition to cancelled."
    },
    "BARCAN-TAG-11": {
        "name": "Client Perception & Usability Integrity",
        "axes": ["perception", "speed", "latency", "mobile", "responsive", "error-message", "clarity"],
        "summary": "System responses and validation errors must be deterministic, human-readable, and free of internal leaks."
    },
    "BARCAN-TAG-12": {
        "name": "Social Contract & Multi-Tenant Boundaries",
        "axes": ["tenant", "master", "salon", "auth", "permission", "isolation", "role"],
        "summary": "Multi-tenant boundaries between independent masters and salons must be strictly isolated."
    }
}

# Adjacency graph representing ontological proximity between Barcan tags
GRAPH_EDGES = {
    "BARCAN-TAG-00": ["BARCAN-TAG-01", "BARCAN-TAG-06", "BARCAN-TAG-07", "BARCAN-TAG-08"],
    "BARCAN-TAG-01": ["BARCAN-TAG-00", "BARCAN-TAG-02", "BARCAN-TAG-04", "BARCAN-TAG-06", "BARCAN-TAG-10"],
    "BARCAN-TAG-02": ["BARCAN-TAG-01", "BARCAN-TAG-05", "BARCAN-TAG-08"],
    "BARCAN-TAG-03": ["BARCAN-TAG-01", "BARCAN-TAG-11", "BARCAN-TAG-12"],
    "BARCAN-TAG-04": ["BARCAN-TAG-01", "BARCAN-TAG-05", "BARCAN-TAG-06"],
    "BARCAN-TAG-05": ["BARCAN-TAG-02", "BARCAN-TAG-04", "BARCAN-TAG-06"],
    "BARCAN-TAG-06": ["BARCAN-TAG-00", "BARCAN-TAG-01", "BARCAN-TAG-10", "BARCAN-TAG-04"],
    "BARCAN-TAG-07": ["BARCAN-TAG-00", "BARCAN-TAG-06", "BARCAN-TAG-10"],
    "BARCAN-TAG-08": ["BARCAN-TAG-00", "BARCAN-TAG-02", "BARCAN-TAG-06"],
    "BARCAN-TAG-09": ["BARCAN-TAG-06", "BARCAN-TAG-10"],
    "BARCAN-TAG-10": ["BARCAN-TAG-01", "BARCAN-TAG-06", "BARCAN-TAG-09"],
    "BARCAN-TAG-11": ["BARCAN-TAG-03", "BARCAN-TAG-01"],
    "BARCAN-TAG-12": ["BARCAN-TAG-01", "BARCAN-TAG-02", "BARCAN-TAG-03"]
}

# Non-negotiable base layer (always included with weight 1.0)
COMMON_BASE_KERNEL = """### BASE LAYER: EFREMOV UNIVERSAL ARCHITECTURAL INVARIANTS (Weight: 1.00)
- **Invariant ZERO-DRIFT:** Scope isolation is absolute. Under no circumstances modify files, schemas, or controllers unrelated to this task.
- **Invariant FALSIFICATION:** Before declaring code complete, write tests that specifically attempt to break edge cases, race conditions, and boundary conditions.
- **Invariant COMPILER-FIRST:** Architectural discipline resides in compiler, type, and linter constraints, not loose prompt descriptions.
"""

class PhilosopherContextSynthesizer:
    def __init__(self):
        pass

    def compute_task_relevance(self, query: str) -> Dict[str, float]:
        tokens = re.findall(r"\w+", query.lower())
        scores = {}
        for tag, data in TAG_AXES.items():
            score = 0.0
            for axis in data["axes"]:
                for t in tokens:
                    if t == axis or (len(t) > 3 and t in axis) or (len(axis) > 3 and axis in t):
                        score += 0.50
            scores[tag] = min(1.0, score)
        return scores

    def spread_activation(self, base_scores: Dict[str, float]) -> Dict[str, float]:
        # Step 0: Initial activation
        activation = {k: v for k, v in base_scores.items()}
        
        # Hop 1: Attenuation lambda = 0.70
        hop1 = {}
        for node, score in activation.items():
            if score > 0:
                for neighbor in GRAPH_EDGES.get(node, []):
                    attenuated = score * ATTENUATION_LAMBDA
                    hop1[neighbor] = max(hop1.get(neighbor, 0.0), attenuated)
                    
        for node, val in hop1.items():
            activation[node] = max(activation.get(node, 0.0), val)

        # Hop 2: Attenuation lambda^2 = 0.49
        hop2 = {}
        for node, score in hop1.items():
            if score > 0:
                for neighbor in GRAPH_EDGES.get(node, []):
                    attenuated = score * ATTENUATION_LAMBDA
                    hop2[neighbor] = max(hop2.get(neighbor, 0.0), attenuated)

        for node, val in hop2.items():
            activation[node] = max(activation.get(node, 0.0), val)

        # Always preserve Code Guardian (TAG-00) for scope safety at minimum 0.75
        activation["BARCAN-TAG-00"] = max(activation.get("BARCAN-TAG-00", 0.0), 0.75)

        return activation

    def synthesize_context(self, task_query: str, token_budget: int = 1500) -> Dict[str, Any]:
        base_scores = self.compute_task_relevance(task_query)
        activations = self.spread_activation(base_scores)

        # Filter by threshold and sort by descending activation weight
        ranked_nodes = [
            (tag, weight) for tag, weight in activations.items() 
            if weight >= ACTIVATION_THRESHOLD
        ]
        ranked_nodes.sort(key=lambda x: x[1], reverse=True)

        # Assemble markdown slice proportional to budget (roughly 4 chars per token)
        char_budget = token_budget * 4
        output_lines = [
            "# Dmitry Efremov Systems Architecture Hub",
            "> Tailored Invariant Slice generated by Philosopher Engine (Graph Attenuation λ=0.70)",
            f"> Context Focus: \"{task_query}\" | Token Budget: {token_budget}\n",
            COMMON_BASE_KERNEL
        ]

        current_chars = sum(len(l) for l in output_lines)

        output_lines.append("### TAILORED ARCHITECTURAL INVARIANTS (Decaying Graph Slice)")

        activated_details = []

        for tag, weight in ranked_nodes:
            tag_meta = TAG_AXES[tag]
            level_name = "EPICENTER (Primary)" if weight >= 0.85 else ("LEVEL 1 (Connected)" if weight >= 0.55 else "LEVEL 2 (Attenuated)")
            
            block = (
                f"- **{tag} - {tag_meta['name']}** [Weight: {weight:.2f} | {level_name}]:\n"
                f"  *Core Invariant:* {tag_meta['summary']}\n"
            )
            
            if current_chars + len(block) > char_budget:
                break
                
            output_lines.append(block)
            current_chars += len(block)
            activated_details.append({
                "tag": tag,
                "name": tag_meta["name"],
                "weight": round(weight, 2),
                "level": level_name
            })

        output_lines.append("\n### PR VERIFICATION OBLIGATION")
        output_lines.append(
            "1. PR diff MUST strictly contain only files relevant to the declared epicenter.\n"
            "2. PR description MUST cite compliance with the above weighted invariants.\n"
            "3. Autonomous unit tests MUST accompany the code, proving resilience against concurrent collisions."
        )

        full_markdown = "\n".join(output_lines)
        estimated_tokens = len(full_markdown) // 4

        return {
            "task": task_query,
            "token_budget": token_budget,
            "estimated_tokens": estimated_tokens,
            "activated_node_count": len(activated_details),
            "activated_nodes": activated_details,
            "markdown": full_markdown
        }

synthesizer = PhilosopherContextSynthesizer()

if __name__ == "__main__":
    # Test execution
    res = synthesizer.synthesize_context("Beauty booking service with master scheduling and concurrency", token_budget=1500)
    print("Activated nodes:", len(res["activated_nodes"]))
    for n in res["activated_nodes"]:
        print(f"  * {n['tag']} ({n['weight']}) -> {n['level']}")
    print("\nEstimated tokens:", res["estimated_tokens"])
