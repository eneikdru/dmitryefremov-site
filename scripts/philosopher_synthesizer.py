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
ACTIVATION_THRESHOLD = 0.28

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
            "слоты", "интервал", "время", "буфер", "мастер", "календарь", "сеанс", "расписание"
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
            "idempotency", "token", "идентификатор", "токен", "ключ", "ссылка", "артикул", "код", "sku"
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
            "телефон", "клиент", "валидация", "лари", "формат", "грузия", "адрес", "цена", "стоимость"
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
            "гонщик", "гонка", "параллельно", "блокировка", "конкурентность", "одновременно", "перехват", "коллизия"
        ],
        "advice": (
            "Многопользовательские состояния и конкурентные запросы на один ресурс сериализуются атомарно. "
            "При попытке параллельного захвата одного ресурса второй запрос обязан завершаться детерминированным отказом 409 Conflict."
        ),
        "checklist": "Тест с параллельными потоками подтверждает успешное создание ровно одной брони и отказ для остальных."
    },
    "NODE_STATE_MACHINE": {
        "pattern": "Детерминированный конечный автомат (Deterministic State Machine & Transition Guards)",
        "axes": [
            "state", "transition", "status", "cancel", "flow", "lifecycle", "confirmed", "dag",
            "статус", "переход", "жизненный", "цикл", "отмена", "подтверждение", "этап", "стадия"
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
            "тест", "сид", "инициализация", "верификация", "проверка", "пустой", "старт", "база", "каталог"
        ],
        "advice": (
            "Принцип полноты рантайма: сервис обязан быть автономным. При первом запуске на чистой базе данных "
            "система должна автоматически засевать минимальный верифицируемый набор сущностей (демо-каталог, позиции, доступные слоты), "
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
        "checklist": "Пользователь может протестировать полный сценарий без наличия сторонних ключей OAuth."
    },
    "NODE_MULTI_TENANT_ISOLATION": {
        "pattern": "Мультитенантная изоляция границ (Multi-Tenant Boundary Isolation)",
        "axes": [
            "tenant", "salon", "master", "isolation", "workspace", "multi", "boundary", "branch",
            "мультитенант", "изоляция", "филиал", "разграничение", "группа", "отделение"
        ],
        "advice": (
            "Данные и расписания независимых филиалов, групп и специалистов изолируются на уровне запросов. "
            "Записи одной организационной единицы ни при каких условиях не должны пересекаться или утекать в другую."
        ),
        "checklist": "Запросы расписания и бронирования строго фильтруются по идентификатору филиала/группы."
    },
    # Specialized Domain Extensions
    "NODE_PERISHABLE_INVENTORY": {
        "pattern": "Резервирование скоропортящихся остатков и штучных изделий (Perishable Inventory & Finite Stock Reservation)",
        "axes": [
            "flower", "flowers", "bouquet", "doll", "dolls", "stock", "shelf", "freshness", "warehouse",
            "цветы", "букет", "флорист", "свежесть", "кукла", "куклы", "кукол", "кукольный", "ручная",
            "работа", "остатки", "склад", "порча", "кафе", "десерт", "выпечка", "ингредиенты", "авторск", "штучн", "издели", "коллекц", "пошив"
        ],
        "advice": (
            "Моделируйте учет физических штучных товаров и скоропортящихся позиций через временные блокировки инвентаря (Inventory Hold with TTL). "
            "При добавлении редкой авторской куклы или букета в корзину позиция резервируется на фиксированное время (например, 15 минут). "
            "Если оплата не поступила, резерв автоматически снимается без блокировки склада."
        ),
        "checklist": "Остаток товара уменьшается атомарно; истечение TTL корзины возвращает позицию в открытый каталог."
    },
    "NODE_FLIGHT_EXTERNAL_TELEMETRY": {
        "pattern": "Асинхронная сверка внешней телеметрии рейсов (Asynchronous Telemetry Ingestion & Polling Reconciliation)",
        "axes": [
            "transfer", "airport", "flight", "terminal", "arrival", "telemetry", "shuttle", "chauffeur",
            "трансфер", "аэропорт", "рейс", "самолет", "прилет", "терминал", "задержка", "встреча", "табличка", "водитель"
        ],
        "advice": (
            "Время подачи трансфера привязывается не к статическому времени, а к динамическому идентификатору авиарейса (Flight Number). "
            "Фоновый воркер периодически опрашивает статус прилета (Aviation Telemetry API) и автоматически сдвигает время встречи "
            "при задержке или ранней посадке рейса, защищая водителя и клиента от простоев."
        ),
        "checklist": "Смена статуса рейса на 'Delayed' автоматически корректирует время подачи автомобиля в расписании."
    },
    "NODE_DEPOSIT_COLLATERAL_ESCROW": {
        "pattern": "Двухфазный залоговый эскроу и проверка прав (Two-Phase Authorization Escrow & Driver Attestation)",
        "axes": [
            "car", "rental", "fleet", "deposit", "escrow", "license", "collateral", "vehicle", "mileage",
            "аренда", "прокат", "машина", "автомобиль", "автопарк", "залог", "депозит", "права", "удостоверение", "пробег", "страховка"
        ],
        "advice": (
            "Операции с высокой ответственностью (аренда автотранспорта) разделяются на фазу валидации прав/страховки "
            "и двухфазную холдирующую авторизацию депозита (Card Pre-Authorization / Escrow Hold). "
            "Списание средств за фактический пробег и возврат залога производятся только после подписания акта возврата автомобиля."
        ),
        "checklist": "Бронирование автомобиля блокируется без подтверждения валидности водительских прав и успешного холда залога."
    },
    "NODE_CAPACITY_CONSTRAINED_RESOURCE": {
        "pattern": "Квотирование посадки и столиков (Discrete Capacity Allocation & Table Seating)",
        "axes": [
            "table", "cafe", "coffee", "restaurant", "seats", "menu", "check", "order",
            "кафе", "кофейня", "столик", "посадка", "стол", "меню", "предзаказ", "блюда", "напитки", "чек", "официант"
        ],
        "advice": (
            "Для бронирования посадочных мест (столики в кафе и ресторанах) используйте дискретное распределение столов по вместимости. "
            "Каждый стол моделируется как атомарный ресурс с временным интервалом и вместимостью (min_guests, max_guests). "
            "При исчерпании доступных столов на выбранное время посетителю предлагаются ближайшие альтернативные временные окна."
        ),
        "checklist": "Попытка бронирования занятого столика отклоняется с 409 Conflict; система предлагает альтернативные свободные часы."
    },
    "NODE_ATTENDANCE_SUBSCRIPTION": {
        "pattern": "Учет абонементов и бронь ковриков (Class Pass Subscriptions & Mat Reservation)",
        "axes": [
            "yoga", "fitness", "pilates", "mat", "subscription", "pass", "coach", "trainer", "studio",
            "йога", "фитнес", "пилатес", "коврик", "абонемент", "тренировка", "практика", "инструктор", "тренер", "зал"
        ],
        "advice": (
            "Групповые занятия в студиях (йога, фитнес) моделируются через квотированный пул мест с проверкой баланса абонемента. "
            "При бронировании места списывается одно занятие с активного абонемента с проверкой срока действия (Expiration Date). "
            "При отмене занятия ранее чем за 2 часа списание автоматически аннулируется с возвратом баланса."
        ),
        "checklist": "Запись на групповую практику уменьшает счетчик свободных ковриков; отсутствие баланса абонемента блокирует запись."
    },
    "NODE_CHILDCARE_ENROLLMENT": {
        "pattern": "Учет воспитанников и родительский доступ (Childcare Enrollment, Attendance & Guardian Delegation)",
        "axes": [
            "kindergarten", "child", "children", "kids", "preschool", "guardian", "parent", "attendance", "group",
            "детский", "сад", "садик", "ребенок", "дети", "воспитанник", "родитель", "родители", "воспитатель", "группа", "посещаемость", "питание"
        ],
        "advice": (
            "Дошкольное учреждение требует строгой модели делегирования прав (Guardian Delegation): ребенок привязывается к доверенным законным представителям. "
            "В системе ведется ежедневный табель посещаемости и питания с проверкой медицинского допуска (справка после болезни). "
            "Доступ к фотоотчетам и расписанию строго изолирован в разрезе конкретной возрастной группы."
        ),
        "checklist": "Отметка о прибытии ребенка в табеле обновляет статус питания; неавторизованные контакты не имеют доступа к данным группы."
    },
    "NODE_MEDICAL_COMPLIANCE_RECORD": {
        "pattern": "Неизменяемый журнал ветеринарных и медицинских карт (Immutable Medical Record & Patient History)",
        "axes": [
            "vet", "clinic", "pet", "dog", "cat", "animal", "vaccine", "doctor", "health", "hospital",
            "ветеринар", "клиника", "питомец", "животное", "собака", "кошка", "вакцинация", "чипирование", "веткарта", "терапевт", "хирургия", "болезнь"
        ],
        "advice": (
            "Ветеринарная помощь требует ведения непрерывной медицинской истории животного (Append-Only Patient Ledger). "
            "Каждый прием, вакцинация или назначение лекарств подписываются идентификатором лечащего ветврача и сохраняются без возможности удаления. "
            "Вакцинация автоматически рассчитывает дату следующей ревакцинации с постановкой напоминания владельцу."
        ),
        "checklist": "Запись о проведенной вакцинации формирует запись в ветпаспорте с планируемой датой следующей ревакцинации."
    },
    "NODE_CONSENT_TATTOO_PROCEDURE": {
        "pattern": "Юридическое согласие 18+ и бронирование сеансов (Age Attestation & Procedure Consent Ledger)",
        "axes": [
            "tattoo", "piercing", "ink", "sketch", "consent", "session", "master", "18", "age",
            "тату", "пирсинг", "татуировка", "эскиз", "согласие", "сеанс", "мастер", "18", "возраст", "стерилизация"
        ],
        "advice": (
            "Процедуры модификации тела требуют юридически значимого подтверждения совершеннолетия (18+ Age Attestation) "
            "и подписания информированного согласия на процедуру и аллергопробу перед фиксацией сеанса мастера. "
            "Эскизы связываются с сессией как защищенные вложения с контролем авторских прав."
        ),
        "checklist": "Бронирование сеанса татуировки блокируется без явного согласия с условиями 18+ и протоколами безопасности."
    },
    "NODE_WORKFLOW_REPAIR_DIAGNOSTICS": {
        "pattern": "Конвейер ответственного хранения и серийной диагностики (Serialized Custody & Diagnostic State Pipeline)",
        "axes": [
            "repair", "electronics", "hardware", "imei", "serial", "diagnostic", "soldering", "warranty", "part", "device",
            "ремонт", "электроника", "телефон", "ноутбук", "диагностика", "пайка", "серийный", "запчасти", "гарантия", "приемка", "акт", "устройство"
        ],
        "advice": (
            "Ремонт техники строится вокруг уникального серийного номера (IMEI/Serial Number) и строгого конвейера ответственного хранения: "
            "[Принят] -> [Диагностика] -> [Согласование сметы] -> [Ожидание запчастей] -> [Пайка/Сборка] -> [Контроль качества] -> [Выдан]. "
            "Переход на этап ремонта невозможен без зафиксированного акта согласования стоимости клиентом."
        ),
        "checklist": "Каждое изменение статуса аппарата сопровождается фиксацией ответственного инженера и штампа времени."
    }
}

GRAPH_EDGES = {
    "NODE_SCOPE_ISOLATION": ["NODE_NOMINAL_IDENTIFIERS", "NODE_LOCATION_AGNOSTIC_ROUTING"],
    "NODE_TEMPORAL_INTERVAL": ["NODE_CONCURRENCY_MUTEX", "NODE_STATE_MACHINE", "NODE_SELF_SEEDING_VERIFICATION"],
    "NODE_NOMINAL_IDENTIFIERS": ["NODE_TEMPORAL_INTERVAL", "NODE_CONCURRENCY_MUTEX", "NODE_WORKFLOW_REPAIR_DIAGNOSTICS"],
    "NODE_CLIENT_LOCALIZATION": ["NODE_GRACEFUL_IDENTITY_FALLBACK", "NODE_TEMPORAL_INTERVAL"],
    "NODE_CONCURRENCY_MUTEX": ["NODE_TEMPORAL_INTERVAL", "NODE_STATE_MACHINE", "NODE_CAPACITY_CONSTRAINED_RESOURCE"],
    "NODE_STATE_MACHINE": ["NODE_CONCURRENCY_MUTEX", "NODE_WORKFLOW_REPAIR_DIAGNOSTICS", "NODE_DEPOSIT_COLLATERAL_ESCROW"],
    "NODE_SELF_SEEDING_VERIFICATION": ["NODE_GRACEFUL_IDENTITY_FALLBACK", "NODE_LOCATION_AGNOSTIC_ROUTING"],
    "NODE_LOCATION_AGNOSTIC_ROUTING": ["NODE_GRACEFUL_IDENTITY_FALLBACK", "NODE_SELF_SEEDING_VERIFICATION"],
    "NODE_GRACEFUL_IDENTITY_FALLBACK": ["NODE_CLIENT_LOCALIZATION", "NODE_SELF_SEEDING_VERIFICATION"],
    "NODE_MULTI_TENANT_ISOLATION": ["NODE_TEMPORAL_INTERVAL", "NODE_SCOPE_ISOLATION"],
    "NODE_PERISHABLE_INVENTORY": ["NODE_STATE_MACHINE", "NODE_NOMINAL_IDENTIFIERS", "NODE_CONCURRENCY_MUTEX"],
    "NODE_FLIGHT_EXTERNAL_TELEMETRY": ["NODE_TEMPORAL_INTERVAL", "NODE_CLIENT_LOCALIZATION"],
    "NODE_DEPOSIT_COLLATERAL_ESCROW": ["NODE_STATE_MACHINE", "NODE_NOMINAL_IDENTIFIERS", "NODE_CONCURRENCY_MUTEX"],
    "NODE_CAPACITY_CONSTRAINED_RESOURCE": ["NODE_CONCURRENCY_MUTEX", "NODE_TEMPORAL_INTERVAL"],
    "NODE_MEDICAL_COMPLIANCE_RECORD": ["NODE_STATE_MACHINE", "NODE_CLIENT_LOCALIZATION"],
    "NODE_WORKFLOW_REPAIR_DIAGNOSTICS": ["NODE_STATE_MACHINE", "NODE_NOMINAL_IDENTIFIERS"]
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
            if score > 0.35:
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
            f'> Анализ архитектурного контекста: "{task_query}"\n',
            COMMON_BASE_KERNEL,
            "### РЕКОМЕНДОВАННЫЕ ИНЖЕНЕРНЫЕ ПАТТЕРНЫ И ПРАКТИКИ РЕАЛИЗАЦИИ"
        ]

        structured_patterns = []
        for node_id, weight in ranked:
            node_data = PATTERN_ONTOLOGY.get(node_id, {})
            pattern_title = node_data.get("pattern", "Архитектурный паттерн")
            advice_text = node_data.get("advice", "")
            checklist = node_data.get("checklist", "")

            level = "Критический приоритет" if weight >= 0.85 else ("Высокий приоритет" if weight >= 0.55 else "Рекомендованный паттерн")
            advice_lines.append(f"\n#### {pattern_title} [{level}]")
            advice_lines.append(f"**Архитектурный совет:** {advice_text}")
            advice_lines.append(f"**Критерий приёмки:** {checklist}")

            structured_patterns.append({
                "node_id": node_id,
                "pattern": pattern_title,
                "priority": level,
                "weight": round(weight, 3),
                "architectural_advice": advice_text,
                "acceptance_checklist": checklist
            })

        return {
            "status": "ADVISORY_SYNTHESIZED",
            "task": task_query,
            "question": question,
            "dominant_pattern": structured_patterns[0]["pattern"] if structured_patterns else "Universal Standards",
            "patterns_count": len(structured_patterns),
            "patterns": structured_patterns,
            "markdown": "\n".join(advice_lines)
        }

synthesizer = PhilosopherContextSynthesizer()

if __name__ == "__main__":
    test_queries = [
        "Кафе и кофейня: меню, столики, бронирование столиков и предзаказ",
        "Цветочный магазин: букеты, доставка к точному часу, учет свежести цветов",
        "Аренда машин: автопарк, проверка прав, залог и страховка",
        "Трансфер из аэропорта: номер авиарейса, терминал, отслеживание задержек",
        "Частный детский сад: возрастные группы, расписание, медсправки",
        "Магазин авторских кукол: штучные коллекционные изделия, пошив на заказ",
        "Ветеринарная клиника: прием терапевта, чипирование, вакцинация",
        "Студия йоги и пилатеса: вместимость зала, коврики, абонементы",
        "Ремонт электроники: приемка по IMEI, диагностика, статус пайки",
        "Салон тату и пирсинга: эскизы, согласие 18+, стерилизация, сеансы"
    ]
    for q in test_queries:
        res = synthesizer.synthesize_advice(q)
        print(f"Query: {q[:30]}... -> Dominant: {res['dominant_pattern']} (Patterns: {res['patterns_count']})")
