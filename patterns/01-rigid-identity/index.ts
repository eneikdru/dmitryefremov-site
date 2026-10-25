/**
 * Pattern 01: Rigid Identity & Branded Types
 * Epistemic source: Ruth Barcan Marcus (Direct Reference & Necessity of Identity)
 * Architectural synthesis: Dmitry Efremov
 */

declare const BrandId: unique symbol;

/**
 * Номинальный типизированный идентификатор (жесткий десигнатор).
 * Гарантирует непересекаемость ID разных сущностей на этапе сборки.
 */
export type EntityId<Brand extends string> = string & { readonly [BrandId]: Brand };

export type AccountId = EntityId<'Account'>;
export type InvoiceId = EntityId<'Invoice'>;
export type OrderId = EntityId<'Order'>;

export interface ImmutableIdentity<Brand extends string> {
  readonly id: EntityId<Brand>;
  readonly createdAt: string;
}

export interface AccountState {
  readonly legalName: string;
  readonly taxIdentifier: string;
  readonly status: 'ACTIVE' | 'SUSPENDED' | 'TERMINATED';
}

export interface AccountEntity extends ImmutableIdentity<'Account'> {
  readonly state: AccountState;
}

export function createEntityId<Brand extends string>(rawId: string): EntityId<Brand> {
  if (!rawId || typeof rawId !== 'string' || rawId.trim().length === 0) {
    throw new Error(`[RigidIdentity] Invalid raw identifier: cannot create empty EntityId`);
  }
  return rawId.trim() as EntityId<Brand>;
}

export function isSameEntity<Brand extends string>(
  a: ImmutableIdentity<Brand>,
  b: ImmutableIdentity<Brand>
): boolean {
  return a.id === b.id;
}
