export interface Serializable {
  serialize(): string;
}

export interface Deserializable<T> {
  deserialize(i: Record<string, any>): T;
}
