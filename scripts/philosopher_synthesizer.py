#!/usr/bin/env python3
"""
Architectural Knowledge Engine & Pattern Advisory Synthesizer
Generates tailored engineering guidance using Spreading Activation (lambda=0.70) over ontological patterns.
Strictly sanitizes internal identifiers to expose only industry-standard engineering patterns and actionable architectural advice.
"""

import os
import re
import math
from typing import Dict, List, Any, Optional

CORPUS_DIR = "/opt/EneikProductionSys"
PATTERNS_DIR = os.path.join(CORPUS_DIR, "docs", "philosopher-patterns")

ATTENUATION_LAMBDA = 0.70
ACTIVATION_THRESHOLD = 0.35

# Clean, industry-standard engineering patterns mapped from the ontological graph
# Zero internal naming, pure engineering practices and defect prevention principles.
PATTERN_ONTOLOGY = {
    "NODE_SCOPE_ISOLATION": {
        "pattern": "Изоляция контекста и ограниченный радиус изменений (Bounded Context & Scope Isolation)",
        "axes": [
            "boundary", "review", "scope", "integration", "files", "blast", "radius", "pr", "diff",
            "refactoring", "legacy", "isolation", "граница", "скоуп", "дифф", "изоляция", "файлы"
        ],
        "advice": (
            "Строгая изоляция области изменений: никогда не модифицируйте несвязанные файлы или сторонние схемы. "
            "Каждое изменение должно иметь строго очерченный эпицентр с минимальным радиусом влияния."
        ),
        "checklist": "Изменения касаются только файлов задачи; сторонние модули остаются нетронутыми."
    },
    "NODE_TEMPORAL_INTERVAL": {
        "pattern": "Детерминированная модель интервалов и идемпотентность (Temporal Interval Model & Atomic Mutex)",
        "axes": [
            "booking", "schedule", "calendar", "time", "interval", "duration", "slot", "appointment",
            "overlap", "buffer", "domain", "types", "tbilisi", "timezone", "бронирование", "запись",
            "слоты", "интервал", "время", "буфер", "мастер", "календарь", "салон"
        ],
        "advice": (
            "Моделируйте временные слоты как полуоткрытые интервалы [t_start, t_end + buffer). "
            "Пересечение фиксируется условием max(start1, start2) < min(end1, end2). "
            "Слот резервируется атомарно в режиме немедленной сериализации транзакций (BEGIN IMMEDIATE / SELECT FOR UPDATE), "
            "что исключает возникновение двойных бронирований на уровне базы данных."
        ),
        "checklist": "Буферное время включено в интервал занятости; проверка пересечений выполняется внутри транзакции записи."
    },
    "NODE_NOMINAL_IDENTIFIERS": {
        "pattern": "Иммутабельные идентификаторы и стабильность ссылок (Immutable Nominal Identifiers)",
        "axes": [
            "id", "uuid", "naming", "reference", "identifier", "api", "key", "immutable", "slug",
            "idempotency", "token", "идентификатор", "токен", "ключ", "ссылка"
        ],
        "advice": (
            "Идентификаторы сущностей и типов номинально инвариантны. "
            "Используйте криптографически стойкие неизменяемые ключи идемпотентности (Idempotency-Key) для операций создания, "
            "гарантируя безопасный повтор сетевых запросов без дублирования записей."
        ),
        "checklist": "Повторный запрос с тем же Idempotency-Key возвращает исходный результат без создания дубликата."
    },
    "NODE_CLIENT_LOCALIZATION": {
        "pattern": "Локализация и валидация клиентских форматов (Localization & Format Invariants)",
        "axes": [
            "phone", "email", "client", "georgia", "gel", "lari", "validation", "regex", "currency",
            "телефон", "клиент", "валидация", "лари", "формат", "грузия"
        ],
        "advice": (
            "Клиентские форматы валидируются на границе системы жесткими регулярными выражениями (например, телефонный формат региона). "
            "Цены и финансовые единицы фиксируются в минимальных неделимых единицах (minor units/тетри), чтобы исключить погрешности чисел с плавающей точкой."
        ),
        "checklist": "Некорректный номер телефона или формат даты отвергается на этапе валидации схемы HTTP 422."
    },
    "NODE_CONCURRENCY_MUTEX": {
        "pattern": "Атомарная сериализация конкурентных запросов (Atomic Concurrency Serialization)",
        "axes": [
            "concurrency", "race", "parallel", "lock", "thread", "transaction", "mutex", "double",
            "гонщик", "гонка", "параллельно", "блокировка", "конкурентность", "одновременно"
        ],
        "advice": (
            "Многопользовательские состояния и конкурентные запросы на один ресурс сериализуются атомарно. "
            "При попытке параллельного захвата одного слота второй запрос обязан завершаться детерминированным отказом 409 Conflict."
        ),
        "checklist": "Тест с параллельными потоками подтверждает успешное создание ровно одной брони и отказ для остальных."
    },
    "NODE_STATE_MACHINE": {
        "pattern": "Детерминированный конечный автомат (Deterministic State Machine & Transition Guards)",
        "axes": [
            "state", "transition", "status", "cancel", "flow", "lifecycle", "confirmed", "dag",
            "статус", "переход", "жизненный", "цикл", "отмена", "подтверждение"
        ],
        "advice": (
            "Жизненный цикл сущности моделируется как направленный ациклический граф допустимых переходов. "
            "Недопустимые переходы (например, отмена уже завершенного визита или повторная отмена) запрещаются на уровне бизнес-правил."
        ),
        "checklist": "Отмена подтвержденной брони освобождает слот; повторная отмена идемпотентна или корректно отклоняется."
    },
    "NODE_SELF_SEEDING_VERIFICATION": {
        "pattern": "Самодостаточная инициализация данных (Self-Seeding Runtime & Executable Falsification)",
        "axes": [
            "test", "falsification", "seed", "mock", "init", "verification", "sqlite", "catalog", "empty",
            "тест", "сид", "инициализация", "верификация", "проверка", "пустой", "старт", "база"
        ],
        "advice": (
            "Принцип полноты рантайма: сервис обязан быть автономным. При первом запуске на чистой базе данных "
            "система должна автоматически засевать минимальный верифицируемый набор сущностей (демо-мастера, услуги, доступные слоты), "
            "чтобы сервис был немедленно готов к демонстрации и тестированию без ручных манипуляций."
        ),
        "checklist": "При старте с новой БД сервис сразу отображает демонстрационные данные без пустых экранов."
    },
    "NODE_LOCATION_AGNOSTIC_ROUTING": {
        "pattern": "Независимость от точки монтирования и стабильность контрактов (Location-Agnostic Subpath Mounting)",
        "axes": [
            "contract", "api", "schema", "openapi", "version", "path", "proxy", "subpath", "mount",
            "путь", "прокси", "документация", "контракт", "монтирование", "схема", "swagger"
        ],
        "advice": (
            "Принцип сохранения истинности навигации: веб-интерфейс, ссылки документации (/docs) и вызовы fetch() "
            "не должны зависеть от точки монтирования приложения (корень или подпуть reverse proxy). "
            "Используйте относительную адресацию (api/...) или учитывайте префикс окружения (ROOT_PATH)."
        ),
        "checklist": "Интерфейс и Swagger UI корректно загружают ресурсы как в корне домена, так и за reverse proxy."
    },
    "NODE_GRACEFUL_IDENTITY_FALLBACK": {
        "pattern": "Безупречная деградация аутентификации (Graceful Identity Facade & Deadlock-Free UX)",
        "axes": [
            "ui", "ux", "auth", "login", "oauth", "guest", "fallback", "client", "deadlock", "button",
            "вход", "авторизация", "гость", "демо", "кнопка", "тупик", "интерфейс"
        ],
        "advice": (
            "Исключайте тупиковые состояния интерфейса (Deadlock-Free User Experience). "
            "Если внешние провайдеры аутентификации (OAuth) не сконфигурированы в окружении, система обязана "
            "предоставлять детерминированный гостевой доступ (Guest / Demo Token), сохраняя сессионный контекст для прохождения всего пользовательского пути."
        ),
        "checklist": "Пользователь может протестировать полный сценарий записи без наличия сторонних ключей OAuth."
    },
    "NODE_MULTI_TENANT_ISOLATION": {
        "pattern": "Мультитенантная изоляция границ (Multi-Tenant Boundary Isolation)",
        "axes": [
            "tenant", "salon", "master", "isolation", "workspace", "multi", "boundary",
            "мультитенант", "изоляция", "салон", "разграничение"
        ],
        "advice": (
            "Данные и расписания независимых мастеров и организаций изолируются на уровне запросов. "
            "Слоты одного мастера ни при каких условиях не должны влиять на занятость другого."
        ),
        "checklist": "Запросы расписания и бронирования строго фильтруются по идентификатору мастера/арендатора."
    }
}

GRAPH_EDGES = {
    "NODE_SCOPE_ISOLATION": ["NODE_NOMINAL_IDENTIFIERS", "NODE_LOCATION_AGNOSTIC_ROUTING"],
    "NODE_TEMPORAL_INTERVAL": ["NODE_CONCURRENCY_MUTEX", "NODE_STATE_MACHINE", "NODE_SELF_SEEDING_VERIFICATION"],
    "NODE_NOMINAL_IDENTIFIERS": ["NODE_TEMPORAL_INTERVAL", "NODE_CONCURRENCY_MUTEX"],
    "NODE_CLIENT_LOCALIZATION": ["NODE_GRACEFUL_IDENTITY_FALLBACK", "NODE_TEMPORAL_INTERVAL"],
    "NODE_CONCURRENCY_MUTEX": ["NODE_TEMPORAL_INTERVAL", "NODE_STATE_MACHINE"],
    "NODE_STATE_MACHINE": ["NODE_CONCURRENCY_MUTEX", "NODE_TEMPORAL_INTERVAL"],
    "NODE_SELF_SEEDING_VERIFICATION": ["NODE_GRACEFUL_IDENTITY_FALLBACK", "NODE_LOCATION_AGNOSTIC_ROUTING"],
    "NODE_LOCATION_AGNOSTIC_ROUTING": ["NODE_GRACEFUL_IDENTITY_FALLBACK", "NODE_SELF_SEEDING_VERIFICATION"],
    "NODE_GRACEFUL_IDENTITY_FALLBACK": ["NODE_CLIENT_LOCALIZATION", "NODE_SELF_SEEDING_VERIFICATION"],
    "NODE_MULTI_TENANT_ISOLATION": ["NODE_TEMPORAL_INTERVAL", "NODE_SCOPE_ISOLATION"]
}

COMMON_BASE_KERNEL = """### БАЗОВЫЕ УНИВЕРСАЛЬНЫЕ АРХИТЕКТУРНЫЕ ИНВАРИАНТЫ
- **Изоляция изменений (Scope Isolation):** Запрещено модифицировать файлы и контроллеры вне заявленного эпицентра задачи.
- **Обязательная фальсификация (Executable Verification):** Код признается готовым только при наличии исполняемых негативных тестов краевых случаев и гонок.
- **Дисциплина типов и контрактов (Contract-First):** Все интерфейсы валидируются схемами и строгими типами, исключающими недопустимые состояния.
"""

class PhilosopherContextSynthesizer:
    def __init__(self):
        pass

    def compute_task_relevance(self, query: str) -> Dict[str, float]:
        tokens = re.findall(r"\w+", query.lower())
        scores = {}
        for node_id, data in PATTERN_ONTOLOGY.items():
            score = 0.0
            axes = data.get("axes", [])
            for token in tokens:
                for axis in axes:
                    if token == axis:
                        score += 1.0
                    elif len(token) >= 4 and len(axis) >= 4 and (token in axis or axis in token):
                        score += 0.5
            scores[node_id] = score

        max_score = max(scores.values()) if scores and max(scores.values()) > 0 else 1.0
        return {node_id: s / max_score for node_id, s in scores.items()}

    def spread_activation(self, base_scores: Dict[str, float]) -> Dict[str, float]:
        activations = dict(base_scores)
        for node_id, score in base_scores.items():
            if score > 0.4:
                neighbors = GRAPH_EDGES.get(node_id, [])
                for neighbor in neighbors:
                    propagated = score * ATTENUATION_LAMBDA
                    if propagated > activations.get(neighbor, 0.0):
                        activations[neighbor] = propagated
        return activations

    def synthesize_advice(self, task_query: str, question: Optional[str] = None) -> Dict[str, Any]:
        combined_query = f"{task_query} {question or ''}".strip()
        base_scores = self.compute_task_relevance(combined_query)
        activations = self.spread_activation(base_scores)

        ranked = [
            (node_id, weight) for node_id, weight in activations.items()
            if weight >= ACTIVATION_THRESHOLD
        ]
        ranked.sort(key=lambda x: x[1], reverse=True)

        advice_lines = [
            "# Архитектурная консультация и руководящие инженерные паттерны",
            f"> Анализ архитектурного контекста: \"{task_query}\"\n",
            COMMON_BASE_KERNEL,
            "### РЕКОМЕНДОВАННЫЕ ИНЖЕНЕРНЫЕ ПАТТЕРНЫ И ПРАКТИКИ РЕАЛИЗАЦИИ"
        ]

        structured_patterns = []
        for node_id, weight in ranked:
            node_data = PATTERN_ONTOLOGY.get(node_id, {})
            pattern_title = node_data.get("pattern", "Архитектурный паттерн")
            advice_text = node_data.get("advice", "")
            checklist = node_data.get("checklist", "")

            level = "Критический приоритет" if weight >= 0.9 else ("Высокий приоритет" if weight >= 0.6 else "Рекомендованный паттерн")
            advice_lines.append(f"\n#### {pattern_title} [{level}]")
            advice_lines.append(f"**Архитектурный совет:** {advice_text}")
            advice_lines.append(f"**Критерий приёмки:** {checklist}")

            structured_patterns.append({
                "pattern": pattern_title,
                "priority": level,
                "architectural_advice": advice_text,
                "acceptance_checklist": checklist
            })

        return {
            "status": "ADVISORY_SYNTHESIZED",
            "task": task_query,
            "markdown": "\n".join(advice_lines),
            "patterns": structured_patterns
        }

    # Backward compatibility with existing handshake caller
    def synthesize_context(self, task_query: str, token_budget: int = 1500) -> Dict[str, Any]:
        res = self.synthesize_advice(task_query)
        return {
            "task": task_query,
            "token_budget": token_budget,
            "estimated_tokens": min(token_budget, len(res["markdown"].split()) * 2),
            "activated_nodes": [{"name": p["pattern"], "weight": 0.8} for p in res["patterns"]],
            "markdown": res["markdown"]
        }

synthesizer = PhilosopherContextSynthesizer()

if __name__ == "__main__":
    test_task = "Создание сервиса онлайн записи к мастеру для салона красоты"
    out = synthesizer.synthesize_advice(test_task, "как сделать авторизацию если нет ключей google")
    print(out["markdown"][:800])
    print("\nTotal patterns synthesized:", len(out["patterns"]))
